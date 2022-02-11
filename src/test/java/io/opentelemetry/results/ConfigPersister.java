package io.opentelemetry.results;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.config.TestConfig;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;

public class ConfigPersister {

    private final Path outfile;

    public ConfigPersister(Path outfile) {
        this.outfile = outfile;
    }

    public void write(TestConfig config) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(config);
        try (PrintStream out = new PrintStream(outfile.toFile())){
            out.print(json);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error opening output json config file: " + outfile, e);
        }
    }
}
