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
			// �n��ť�� IP �s��
			InetAddress group = InetAddress.getByName(GROUP);
			// �ˬd�O�_���X�k���h�}�Ǽ��s��
			if (group.isMulticastAddress()) {
				// �ˬd�O�_���\�}�ҳq�D
				if (datagramChannel.isOpen()) {
					// �]�w�n��ť�����d
					NetworkInterface networkInterface = NetworkInterface.getByName("eth6");
					datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
					// �N DatagramChannel ô������w���𸹡A�n�`�N�A�o���٤��ݭn���w IP �s�աC
					datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));
					// �[�J�n��ť���s��
					MembershipKey key = datagramChannel.join(group, networkInterface);

					while (true) {
						if (key.isValid()) {
							// �}�ҵ��� server �ݰe�Ӫ��ʥ]
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
					System.out.println("�q�D�L�k�}��");
				}
			} else {
				System.out.println("�o�� IP ���O�X�k���h�}�Ǽ� IP");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}