package com.baofeng.ad.mobiletest.capture;

import com.baofeng.ad.mobiletest.common.ConsultBean;
import com.baofeng.ad.mobiletest.common.ConsultMsgMgr;
import com.baofeng.ad.mobiletest.common.ReportMsgMgr;
import net.sourceforge.jpcap.capture.*;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zm.frame.log.Log.log;

public class Capture {
    private static final int INFINITE = -1;
    private static final int PACKET_COUNT = INFINITE;
    private PacketCapture m_pcap;

    private String device;
    private String host;
    private String[] subStrs;
    private String location;

    //private static int count = 0;

    private static Pattern pattern = Pattern.compile("msg=([\\S\\s]+)HTTP/1.1",
            Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

    public Capture(String device, String host, String[] subStrs, String location) {
        this.device = device;
        this.host = host;
        this.subStrs = subStrs;
        this.location = location;
    }

    public void init() {
        // Step 1:  Instantiate Capturing Engine
        m_pcap = new PacketCapture();

        // Step 2:  Check for devices
        if (device.equals("") || device == null) {
            try {
                device = m_pcap.findDevice();
            } catch (CaptureDeviceNotFoundException e) {
                log.error("寻找网卡失败 : " + e.getMessage());
                System.exit(1);
            }
        }

        // Step 3:  Open Device for Capturing (requires root)
        try {
            m_pcap.open(device, true);
        } catch (CaptureDeviceOpenException e) {
            log.error("打开网卡失败 : " + e.getMessage());
            System.exit(1);
        }

        // Step 4:  Add a BPF Filter (see tcpdump documentation)
        //m_pcap.setFilter("", true);

        // Step 5:  Register a Listener for jpcap Packets
        m_pcap.addPacketListener(new PacketHandler());
    }

    public void start() {

        // Step 6:  Capture Data (max. PACKET_COUNT packets)
        try {
            //会阻塞
            m_pcap.capture(PACKET_COUNT);
        } catch (CapturePacketException e) {
            log.error("抓包异常 : " + e.getMessage());
        }

    }

    public void stop() {
        m_pcap.endCapture();
        //m_pcap.close();
    }

    class PacketHandler implements PacketListener {


        @Override
        public void packetArrived(Packet packet) {
            if (packet instanceof TCPPacket) {
                TCPPacket tcpPacket = (TCPPacket) packet;
                String rawData;
                // 如果有代理的话, 会抓到两个相同(近似)的包, 过滤目标端口为80的包
                if (tcpPacket.getDestinationPort() == 80) {
                    try {
                        rawData = URLDecoder.decode(new String(tcpPacket.getTCPData()), "utf-8");
                        /*if (!rawData.trim().equals("")) {
                            log.info("抓到包 :\r\n" + rawData);
                        }*/
                    } catch (Exception e) {
                        log.debug("url decode error : " + e.getMessage());
                        return;
                    }
                    ConsultBean consultBean = new ConsultBean();
                    if (isReportMsg(rawData)) {
                        log.info("抓到报数");
                        Matcher matcher = pattern.matcher(rawData);
                        String jsonData;
                        if(matcher.find()) {
                            //log.debug("Capture " + ++count);
                            jsonData = matcher.group(1);
                            log.info(jsonData);
                            ReportMsgMgr.getInstance().pushMsg(jsonData.trim(), rawData);
                        } else {
                            log.info("但没有找到msg");
                        }
                    }
                    if (isConsultMsg(rawData, consultBean)) {
                        log.info("抓到协商 : \r\n" + consultBean.getRawData());
                        ConsultMsgMgr.getInstance().putMsg(consultBean);
                    }
                }
            }
        }

        private boolean isConsultMsg(String data, ConsultBean consultBean) {
            for (String tmp : new String[] {"wx.houyi.baofeng.net", "/Consultation/web.php"}) {
                if (!tmp.equals("")) {
                    if (!data.contains(tmp.trim())) {
                        return false;
                    }
                }
            }

            Pattern pattern = Pattern.compile("" +
                    "/Consultation/web.php[\\S\\s]+id=(wx_tjbanner[12]?)&[\\S\\s]+xst=(\\d+)&[\\S\\s]+HTTP/1.1");
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                consultBean.setRawData(matcher.group(0));
                consultBean.setId(matcher.group(1));
                consultBean.setXst(matcher.group(2));
            } else {
                return false;
            }

            return true;
        }

        private boolean isReportMsg(String data) {
            if (!data.contains(host)) {
                return false;
            }

            for (String tmp : subStrs) {
                if (!tmp.equals("")) {
                    if (!data.contains(tmp.trim())) {
                        return false;
                    }
                }
            }

            //精确匹配location后面的引号
            if (location != null && !location.equals("")) {
                if (!data.matches("[\\s\\S]*" + location + "\"[\\s\\S]*")) {
                    return false;
                }
            }

            return true;
        }
    }
}

