package idv.kk.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.Enumeration;

public class MulticastServer {
	public static void main(String[] args) {
		final int DEFAULT_PORT = 5555;
		final String GROUP = "225.5.6.6";
		ByteBuffer datetime;

		// 開啟一個 IPv4 的資料封包通道
		try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {
			// 檢查是否成功開啟
			if (datagramChannel.isOpen()) {
				Enumeration e = NetworkInterface.getNetworkInterfaces();
				while(e.hasMoreElements()) {
					NetworkInterface ni = (NetworkInterface) e.nextElement();
				      System.out.println(ni + ":" + ni.getInterfaceAddresses().toString());
				  }
				// 取得名稱為 eth3 的網卡
				NetworkInterface networkInterface = NetworkInterface.getByName("eth6");
				// 設定 multicast 要使用的網卡
				datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
				datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				// 將 DatagramChannel 繫結到指定的埠號，要注意，這時還不需要指定 IP 群組。
				datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));

				for (int i = 0; i < 10; i++) {
					// 每 10 秒送出一筆資料
					try {
						Thread.sleep(10000); // 睡 10 秒
					} catch (InterruptedException ex) {
					}

					datetime = ByteBuffer.wrap(new Date().toString().getBytes());
					// 送出時才指定要送到那個 IP 群組，這表示 bind 後，
					// 還是可以看資料特性，送到不同 IP 群組。
					datagramChannel.send(datetime, new InetSocketAddress(InetAddress.getByName(GROUP), DEFAULT_PORT));
					datetime.flip();
				}
			} else {
				System.out.println("通道開啟失敗");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}