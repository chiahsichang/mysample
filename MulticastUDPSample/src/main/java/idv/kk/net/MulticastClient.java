package idv.kk.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class MulticastClient {

	public static void main(String[] args) {
		final int DEFAULT_PORT = 5555;
		final int MAX_PACKET_SIZE = 65507;
		final String GROUP = "225.5.6.6";

		CharBuffer charBuffer = null;
		Charset charset = Charset.defaultCharset();
		CharsetDecoder decoder = charset.newDecoder();
		ByteBuffer datetime = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

		try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {
			// 要傾聽的 IP 群組
			InetAddress group = InetAddress.getByName(GROUP);
			// 檢查是否為合法的多址傳播群組
			if (group.isMulticastAddress()) {
				// 檢查是否成功開啟通道
				if (datagramChannel.isOpen()) {
					// 設定要傾聽的網卡
					NetworkInterface networkInterface = NetworkInterface.getByName("eth6");
					datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
					// 將 DatagramChannel 繫結到指定的埠號，要注意，這時還不需要指定 IP 群組。
					datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));
					// 加入要傾聽的群組
					MembershipKey key = datagramChannel.join(group, networkInterface);

					while (true) {
						if (key.isValid()) {
							// 開啟等待 server 端送來的封包
							datagramChannel.receive(datetime);
							datetime.flip();
							charBuffer = decoder.decode(datetime);
							System.out.println(charBuffer.toString());
							datetime.clear();
						} else {
							break;
						}
					}
				} else {
					System.out.println("通道無法開啟");
				}
			} else {
				System.out.println("這個 IP 不是合法的多址傳播 IP");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}