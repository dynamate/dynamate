package core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	public static class NullStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
		}

		@Override
		public void write(byte[] b) throws IOException {
		}
	}

	public static class Control {

		private IOTransferHelper helper;

		public Control(IOTransferHelper helper) {
			this.helper = helper;
		}

		public void stop() {
			this.helper.interrupt();
		}
	}

	private static class IOTransferHelper extends Thread {

		private InputStream in;
		private OutputStream out;

		public IOTransferHelper(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024 * 1024];

			try {
				while ((this.in.read(buffer) != -1)) {
					this.out.write(buffer);
				}

				this.in.close();
				this.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static Control asynchronousTransfer(InputStream in, OutputStream out) {
		IOTransferHelper helper = new IOTransferHelper(in, out);
		helper.start();
		return new Control(helper);
	}
}
