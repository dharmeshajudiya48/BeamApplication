package com.dharm.BeamApplication;

import java.util.Arrays;
import java.util.List;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

/**
 * Hello world!
 *
 */
public class WordCountPipeline 
{
    public static void main( String[] args )
    {
    	PipelineOptions options = PipelineOptionsFactory.create();
    	Pipeline pipeline = Pipeline.create(options);
    	
    	// Read from text file
    	PCollection<String> lines = pipeline
    									.apply(
    											"Read from file", 
    											TextIO.read().from("wordcount_input.txt")
    									);
    	//Split each line to words
    	PCollection<List<String>> wordPerLine= lines.apply(MapElements.via(new SimpleFunction<String, List<String>>() {
			private static final long serialVersionUID = 1L;

			@Override
    		public List<String> apply(String input) {
    			return Arrays.asList(input.split(" "));
    		}
		}));
    	PCollection<String> words = wordPerLine.apply(Flatten.<String>iterables());
    	
    	//count wrods
    	PCollection<KV<String, Long>> wordCount =words.apply(Count.<String>perElement());
    	
    	wordCount.apply(MapElements.via(new SimpleFunction<KV<String, Long>, String>() {
    			@Override
    			public String apply(KV<String, Long> input) {
    				return String.format("%s => %s", input.getKey(), input.getValue());
    			}
		}))
    	.apply(TextIO.write().to("wordcount_output"));
    	pipeline.run().waitUntilFinish();
    }
}
