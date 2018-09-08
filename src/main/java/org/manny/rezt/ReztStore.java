package org.manny.rezt;

import com.google.common.hash.Hashing;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.manny.rezt.entity.ReztIndex;
import org.manny.rezt.entity.ReztObject;

/**
 *
 * @author javier
 */
public class ReztStore {
    private final StatefulRedisConnection<String, String> connection;
    private final RedisClient client;
    private final String uri;

    public ReztStore(String uri) {
	this.uri = uri;
	client = RedisClient.create(uri);
	connection = client.connect();
    }

    public ReztIndex create(String ns, InputStream is, Optional<String> alias) throws IOException, NoSuchAlgorithmException {
	RedisCommands<String, String> attrcmd = connection.sync();
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte buffer[] = new byte[4096];
	int n, parts = 1;
	String sha1;

	while ((n = is.read(buffer)) != -1)
	    bos.write(buffer, 0, n);

	sha1 = Hashing.sha1().hashBytes(bos.toByteArray()).toString();

	attrcmd.set("object:content/" + ns + ":" + sha1, bos.toString("UTF-8"));
	attrcmd.set("object:name/" + ns + ":" + sha1, alias.orElse(""));
	attrcmd.set("object:size/" + ns + ":" + sha1, String.valueOf(bos.size()));

	return new ReztIndex(alias.orElse(""), sha1, bos.size(), parts);
    }

    public ReztObject findBySha1(String ns, String sha1) throws UnsupportedEncodingException, FileNotFoundException {
	RedisCommands<String, String> attrcmd = connection.sync();
	String content_ = attrcmd.get("object:content/" + ns + ":" + sha1);

	if (content_ == null)
	    throw new FileNotFoundException("file: " + ns + ":" + sha1);

	byte[] content = content_.getBytes("UTF-8");
	int size = Integer.parseInt(attrcmd.get("object:size/" + ns + ":" + sha1));
	Optional<String> alias = Optional.of(attrcmd.get("object:name/" + ns + ":" + sha1));

	return new ReztObject(new ReztIndex(alias.orElse(""), sha1, size, 1), content);
    }

    public ReztObject findByName(String name) {
	return null;
    }
}