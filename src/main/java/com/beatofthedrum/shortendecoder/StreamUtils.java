/*
** StreamUtils.java
**
** Copyright (c) 2011 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.shortendecoder;

import java.io.IOException;

class StreamUtils
{
	public static int stream_read(ShortenInputStream stream, int size, int[] buf, int startPos)
			throws IOException {
		int bytes_read;
		byte[] bytebuf = new byte[size];

		bytes_read = stream.read(bytebuf, 0, size);
		
		for(int i=0; i < bytes_read; i++)
		{
			buf[startPos + i] = bytebuf[i];
		}
		
		return(bytes_read);

	}

}

