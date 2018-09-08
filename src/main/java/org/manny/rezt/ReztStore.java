package org.manny.rezt;

import java.io.InputStream;
import java.util.Optional;
import org.manny.rezt.entity.ReztIndex;
import org.manny.rezt.entity.ReztObject;

/**
 *
 * @author javier
 */
public class ReztStore {
    private final String config;
    
    public ReztStore(String conf) { config = conf; }

    public ReztIndex create(String ns, InputStream is, Optional<String> alias) {
	String sha1 = "xxx";
	long count = 0, parts = 0;

	return new ReztIndex(alias.orElse(config + 1234), sha1, count, parts);
    }

    public ReztObject findBySha1(String ns, String sha1) {
	// indx = find index: ns:sha1;
	// for 0 ... index.N: part = readPart(sha1.I); bos.write(part); bos.getBA;
	ReztIndex index = new ReztIndex(ns, sha1, 1234, 5);
	byte[] content = ("Hello " + ns).getBytes();

	return new ReztObject(index, content);
    }

    public ReztObject findByName(String name) {
	return null;
    }
}