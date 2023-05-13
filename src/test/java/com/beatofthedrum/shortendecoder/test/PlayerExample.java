/*
** com.beatofthedrum.shortendecoder.cli.DecoderDemo.java
**
** Copyright (c) 2011 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.shortendecoder.test;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class PlayerExample
{

	static SourceDataLine output_stream;
	static java.io.DataInputStream input_stream;

	static int output_opened;
	static String input_file_n = "src/test/resources/fbodemo1.shn";

	static void GetBuffer(ShortenContext sc)
	{
	
		int total_unpacked_bytes = 0;
		int bytes_unpacked;
		
			
		while (true)
		{
			bytes_unpacked = 0;

			bytes_unpacked = (int)ShortenUtils.DecodeBuffer(sc);

			total_unpacked_bytes += bytes_unpacked;

			if (bytes_unpacked > 0)
			{
				output_stream.write(sc.buffer, 0, bytes_unpacked);
			}

			if (bytes_unpacked == 0 || sc.quitActivated==true)
				break;
		} // end of while
		
	}

	public static void main(String [] args)
	{
		ShortenContext sc = new ShortenContext();
		int output_size;
		int total_samples; 
		int sample_rate;
        int num_channels;
		int byteps;
		int bitps;

		output_opened = 0;
		
		try
		{
			java.io.FileInputStream fistream;
			fistream = new java.io.FileInputStream(input_file_n);
			input_stream = new java.io.DataInputStream(fistream);
		}
		catch (java.io.FileNotFoundException fe)
		{
			System.err.println("Cannot open input file: " + input_file_n + " : Error : " + fe);
			System.exit(1);
		}
		
		sc = ShortenUtils.ShortenOpenFileInput(input_stream);
		
		if (sc.error)
        {
            System.err.println("Sorry an error has occured");
            System.err.println(sc.error_message);
            System.exit(1);
        }
		
		// Check if data in Shorten file is standard PCM - strictly speaking for this demo I don't need to do this as this code
		// generates WAV files that support non-PCM, but I'm putting the code in here for clarity/demostration purposes
		
		if(!ShortenUtils.ShortenCheckPCM(sc))
		{
            System.err.println("Sorry, but the data in this Shorten file is not standard PCM data.");
            System.exit(1);		
		}
		
		num_channels = ShortenUtils.ShortenGetNumChannels(sc);

		System.out.println("The Shorten file has " + num_channels + " channels");

        total_samples = ShortenUtils.ShortenGetNumSamples(sc);

		System.out.println("The Shorten file has " + total_samples + " samples");

        byteps = ShortenUtils.ShortenGetBytesPerSample(sc);

		System.out.println("The Shorten file has " + byteps + " bytes per sample");
		
		sample_rate = ShortenUtils.ShortenGetSampleRate(sc);
		
		bitps = ShortenUtils.ShortenGetBitsPerSample(sc);

		AudioFormat audioFormat = new AudioFormat(sample_rate, bitps, num_channels, true, false);

		try
		{
			output_stream = AudioSystem.getSourceDataLine(audioFormat);
			output_stream.open();
			output_stream.start();
			output_opened = 1;
		}
		catch(LineUnavailableException e)
		{
			System.out.println("Cannot open output line with audio format: " + audioFormat + " : Error : " + 3);
			output_opened = 0;
			System.exit(1);
		}

		/* will convert the entire buffer */
		GetBuffer(sc);
		
		ShortenUtils.ShortenCloseFile(sc);

		if (output_opened != 0)
		{
			output_stream.close();
		}
	}

}

