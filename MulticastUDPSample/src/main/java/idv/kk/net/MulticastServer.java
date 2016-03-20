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

		// �}�Ҥ@�� IPv4 ����ƫʥ]�q�D
		try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {
			// �ˬd�O�_���\�}��
			if (datagramChannel.isOpen()) {
				Enumeration e = NetworkInterface.getNetworkInterfaces();
				while(e.hasMoreElements()) {
					NetworkInterface ni = (NetworkInterface) e.nextElement();
				      System.out.println(ni + ":" + ni.getInterfaceAddresses().toString());
				  }
				// ���o�W�٬� eth3 �����d
				NetworkInterface networkInterface = NetworkInterface.getByName("eth6");
				// �]�w multicast �n�ϥΪ����d
				datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
				datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				// �N DatagramChannel ô������w���𸹡A�n�`�N�A�o���٤��ݭn���w IP �s�աC
				datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));

				for (int i = 0; i < 10; i++) {
					// �C 10 ��e�X�@�����
					try {
						Thread.sleep(10000); // �� 10 ��
					} catch (InterruptedException ex) {
					}

					datetime = ByteBuffer.wrap(new Date().toString().getBytes());
					// �e�X�ɤ~���w�n�e�쨺�� IP �s�աA�o��� bind ��A
					// �٬O�i�H�ݸ�ƯS�ʡA�e�줣�P IP �s�աC
					datagramChannel.send(datetime, new InetSocketAddress(InetAddress.getByName(GROUP), DEFAULT_PORT));
					datetime.flip();
				}
			} else {
				System.out.println("�q�D�}�ҥ���");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}