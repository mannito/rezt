package org.manny.rezt.store;

import io.lettuce.core.api.sync.RedisCommands;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author javier
 */
public class ReztObjectInputStream extends InputStream {

    private final ByteBuffer buffer = ByteBuffer.wrap(new byte[ReztStore.PART_SIZE]);
    private final RedisCommands<byte[], byte[]> command;
    private final String fileId;
    private final int parts;
    private int partIndex = 0;

    public ReztObjectInputStream(String id, int parts, RedisCommands<byte[], byte[]> command) {

	fileId = id;
	this.parts = parts;
	this.command = command;
	buffer.position(buffer.limit());
    }

    @Override
    public int read() throws IOException {

	if (buffer.position() == buffer.limit()) {

	    if (partIndex == parts)
		return -1; // EOF

	    buffer.clear();
	    buffer.put(
		command.get(
		    ("content:" + partIndex++ + "/" + fileId).getBytes())
	    );

	    buffer.flip();
	}

	return buffer.get() & 0xFF;
    }
}