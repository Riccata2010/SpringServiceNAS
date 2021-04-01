package com.mio.SpringServiceUpdateNASPhoto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocationSpecifier {

	public static final String MATCH_REGEX1 = "(^.*)(-|_|.)([0-9]{8}).*";
	public static final String MATCH_REGEX2 = "(^[0-9]{8})(-|_|.).*";

	private static final Log LOGGER = LogFactory.getLog(LocationSpecifier.class);

	private static final Pattern patternNumber = Pattern.compile("-?\\d+(\\.\\d+)?");
	private static final DateFormat format_YYYY = new SimpleDateFormat("yyyy");
	private static final DateFormat format_MM = new SimpleDateFormat("MM");

	private File file = null;

	public LocationSpecifier() {
	}

	public LocationSpecifier(File file) {
		this.file = file;
	}

	public String getPath() {
		return moveByName();
	}

	private boolean isNumber(String value) {
		return (value == null) ? false : patternNumber.matcher(value).matches();
	}

	private String move(String yyyy, String mm, File file, Supplier<String> otherwise) {

		int iy = -1, im = -1;

		if (isNumber(yyyy)) {
			iy = Integer.parseInt(yyyy);
		}

		if (isNumber(mm)) {
			im = Integer.parseInt(mm);
		}

		if (iy > 0 && im > 0 && iy >= 1970 && iy < 2100 && im >= 1 && im <= 12) {
			return yyyy + File.separator + mm;
		} else {
			return otherwise.get();
		}
	}

	private String moveByDate() {
		try {

			Path path = Paths.get(file.toString());
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

			String yyyy = null;
			String mm = null;

			try {
				yyyy = format_YYYY.format(attr.lastModifiedTime().toMillis());
				mm = format_MM.format(attr.lastModifiedTime().toMillis());
			} catch (Exception exc1) {
				LOGGER.error("ERROR 1 - EXC: " + exc1);
				try {
					yyyy = format_YYYY.format(attr.creationTime().toMillis());
					mm = format_MM.format(attr.creationTime().toMillis());
				} catch (Exception exc2) {
					LOGGER.error("ERROR 2 - EXC: " + exc2);
				}
			}

			return move(yyyy, mm, file, () -> "default");

		} catch (Exception exc) {
			exc.printStackTrace();
			return "ERROR for file=" + file;
		}
	}

	private String moveByName() {

		String name = file.getName();
		String proposal = getProposalPathByName(name);

		if (proposal != null && proposal.length() > 0) {
			String yyyy = proposal.substring(0, proposal.indexOf(File.separator));
			String mm = proposal.substring(proposal.indexOf(File.separator) + 1);
			return move(yyyy, mm, file, this::moveByDate);
		} else {
			return moveByDate();
		}
	}

	public String getProposalPathByName(String name) {

		String result = "";

		if (name != null && name.length() > 9) {

			String date = null;
			String yyyy = null;
			String mm = null;

			if (name.matches(MATCH_REGEX1)) {

				int index = name.indexOf("-");
				if (index < 0) {
					index = name.indexOf("_");
				}
				if (index < 0) {
					index = name.indexOf(".");
				}
				name = name.substring(index + 1);
			}

			if (name.matches(MATCH_REGEX2)) {

				date = name.substring(0, 8);
				yyyy = date.substring(0, 4);
				mm = date.substring(4, 6);
			}

			result = yyyy + File.separator + mm;
		}

		return result;
	}
}
