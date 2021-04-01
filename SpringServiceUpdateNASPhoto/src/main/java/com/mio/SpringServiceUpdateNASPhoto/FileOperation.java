package com.mio.SpringServiceUpdateNASPhoto;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileOperation implements OperationRunnable<ProgressMessage> {

	private static final Log LOGGER = LogFactory.getLog(FileOperation.class);

	private File file = null;
	private LocationSpecifier specifier = null;
	private ProgressMessage operationResult = null;
	private boolean done = false;
	private int total = 0;

	public FileOperation(File file) {
		this.file = file;
		this.specifier = new LocationSpecifier(file);
	}

	public int getTotal() {
		return total;
	}

	public FileOperation setTotal(int total) {
		this.total = total;
		return this;
	}

	private String moveToPath(String path) {

		String result = "";

		File dirOut = new File(FileComponentManager.DATA_DIR + File.separator + path);
		Path target = Paths.get(dirOut.toString() + File.separator + file.getName());

		if (target.toFile().exists()) {
			if (Utils.getEnvOrDefault("REPLACE_EXISTING", true)) {
				result = moveExecute(dirOut, target);
			} else {
				result = "file=" + file.getName() + " already exists, do nothing.";
				try {
					Files.delete(Paths.get(file.toString()));
				} catch (Exception exc) {
					LOGGER.error("EXC " + exc + " - ERROR for file=" + file);
					exc.printStackTrace();
				}
			}
		} else {
			result = moveExecute(dirOut, target);
		}
		return result;
	}

	private String moveExecute(File dirOut, Path target) {
		String result = "";
		try {
			dirOut.mkdirs();
			if (dirOut.exists()) {
				Path source = Paths.get(file.toString());
				target = Files.copy(source, target, REPLACE_EXISTING);
				if (target.toFile().exists() && target.toFile().length() > 0) {
					Files.delete(source);
					result = "OK for file=" + file.getName();
				} else {
					result = "ERROR for file=" + file.getName() + " can't create a copy!";
				}
			} else {
				result = "ERROR for file=" + file.getName() + " can't create directories!";
			}
		} catch (Exception exc) {
			LOGGER.error("EXC " + exc + " - ERROR for file=" + file);
			exc.printStackTrace();
			result = "ERROR for file=" + file.getName() + " message=" + exc.toString();
		}
		return result;
	}

	private String move() {
		return moveToPath(this.specifier.getPath());
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public ProgressMessage getResult() {
		return operationResult;
	}

	@Override
	public void run() {
		String result = (file.getName() == null) ? "ERROR invalid name file!" : move();
		operationResult = ProgressMessage.onComplete(file.getName(), result, String.valueOf(this.getTotal()));
		done = true;
	}
}
