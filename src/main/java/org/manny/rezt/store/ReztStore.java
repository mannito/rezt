package org.manny.rezt.store;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.manny.rezt.entity.ReztIndex;
import org.manny.rezt.entity.ReztObject;

/**
 *
 * @author javier
 */
public class ReztStore {
    public static final int PART_SIZE = 32*1024*1024; // bytes (<redis-max 512m)

    private final StatefulRedisConnection<String, String> commandConnection;
    private final StatefulRedisConnection<byte[], byte[]> dataConnection;
    private final RedisClient client;
    private final String uri;

    public ReztStore(String uri) {

	this.uri = uri;
	client = RedisClient.create(uri);
	client.setOptions(ClientOptions
		.builder()
		.autoReconnect(false)
		.build()
	);
	commandConnection = client.connect();
	dataConnection = client.connect(new ByteArrayCodec());
    }

    private static String fileId(String ns, String sha1) {
	return ns + ":" + sha1;
    }

    private ReztIndex storeStream(String ns, InputStream is) throws IOException {

	RedisCommands<byte[], byte[]> datacmd = dataConnection.sync();
	RedisCommands<String, String> attrcmd = commandConnection.sync();
	ByteBuffer byteBuff = ByteBuffer.wrap(new byte[PART_SIZE]);
	long size = 0, cid = attrcmd.incr("icontent/" + ns);
	Hasher hasher = Hashing.sha1().newHasher();
	byte[] buffer = new byte[4096*2];
	int parts = 0, n = 0;

	while ((n = is.read(buffer)) != -1) {
	    int remain = byteBuff.remaining();

	    byteBuff.put(buffer, 0, Math.min(n, remain));

	    if (!byteBuff.hasRemaining()) {
		byteBuff.flip();
		datacmd.set(("content:" + parts++ + "/" + ns + ":" + cid).getBytes(),
				Arrays.copyOf(byteBuff.array(), byteBuff.limit()));
		byteBuff.clear();

		if (n > remain)
		    byteBuff.put(buffer, remain, n - remain);
	    }

	    hasher.putBytes(buffer, 0, n);
	    size += n;
	}

	byteBuff.flip();
	if (byteBuff.hasRemaining())
	    datacmd.set(("content:" + parts++ + "/" + ns + ":" + cid)
			    .getBytes(), Arrays.copyOf(byteBuff.array(), byteBuff.limit()));


	long birth = System.currentTimeMillis();
	String sha1 = hasher.hash().toString();
	return new ReztIndex(sha1, size, parts, birth, cid);
    }

    public ReztIndex create(String ns, InputStream is,
			    Optional<String> alias, Set<String> tags)
		throws IOException, NoSuchAlgorithmException {

	RedisCommands<String, String> attrcmd = commandConnection.sync();
	ReztIndex index = storeStream(ns, is);

	String tagKey = "tags/" + fileId(ns, index.getId());
	String indexKey = "index/" + fileId(ns, index.getId());
	String nameKey = "name/" + ns + ":" + alias.orElse(null);

	Map<String, String> imap = new HashMap();
	imap.put("contentId", String.valueOf(index.getContentId()));
	imap.put("birth", String.valueOf(index.getBirth()));
	imap.put("size", String.valueOf(index.getSize()));
	imap.put("parts", String.valueOf(index.getParts()));
	imap.put("tags", tagKey);
	imap.put("sha1", index.getId());

	attrcmd.hmset(indexKey, imap);

	if (!tags.isEmpty()) {
	    attrcmd.sadd(tagKey, tags.toArray(new String[tags.size()]));
	    for (String t: tags)
		attrcmd.sadd("tag/" + ns + ":" +
			Hashing.sha1().hashBytes(t.getBytes("UTF-8")), indexKey);
	}

	attrcmd.sadd(nameKey, indexKey);
	return index;
    }

    public ReztIndex loadIndex(String ns, String sha1) throws FileNotFoundException {
	RedisCommands<String, String> attrcmd = commandConnection.sync();
	Map<String, String> imap = attrcmd.hgetall("index/" + fileId(ns, sha1));

	if (imap == null || imap.isEmpty())
	    throw new FileNotFoundException("file: " + fileId(ns, sha1));

	long birth = Long.valueOf(imap.get("birth"));
	int parts = Integer.valueOf(imap.get("parts"));
	long size = Long.valueOf(imap.get("size"));
	long cid =  Long.valueOf(imap.get("contentId"));
	String sha1Loaded = imap.get("sha1");

	return new ReztIndex(sha1Loaded, size, parts, birth, cid);
    }

    public ReztObject loadBySha1(String ns, String sha1)
	    throws UnsupportedEncodingException, IOException, FileNotFoundException {

	RedisCommands<byte[], byte[]> datacmd = dataConnection.sync();
	ReztIndex index = loadIndex(ns, sha1);

	return new ReztObject(index,
		new ReztObjectInputStream(
			ns + ":" + index.getContentId(), index.getParts(), datacmd)
	);
    }

    public Set<ReztObject> loadByName(String ns, String name, Set<String> tagsFilter)
	    throws UnsupportedEncodingException, IOException, FileNotFoundException {

	RedisCommands<String, String> attrcmd = commandConnection.sync();
	String nameKey = "name/" + ns + ":" + name;
	Set<String> indexes = attrcmd.smembers(nameKey);
	Set<ReztObject> result = new HashSet();

	if (indexes == null || indexes.isEmpty())
	    throw new FileNotFoundException(ns + ":" + name);

	for (String i: indexes) {
	    Map<String, String> imap = attrcmd.hgetall(i);

	    if (!tagsFilter.isEmpty()) {
		Set<String> itags = attrcmd.smembers(imap.get("tags"));

		if (itags == null || itags.isEmpty())
		    continue;

		itags.retainAll(tagsFilter);
		if (itags.isEmpty())
		    continue;
	    }

	    result.add(loadBySha1(ns, imap.get("sha1")));
	}

	return result;
    }
    
    public String status() throws IOException {
	try {

	    if (commandConnection.sync().ping().equals("PONG"))
		return "Connected";

	    throw new IOException("Connection timeout");

	} catch (RedisException rex) {

	    throw new IOException("Connection error: " +
		    rex.getMessage(), rex.getCause());
	}
    }

    public void close() {
	client.shutdown();
    }
}