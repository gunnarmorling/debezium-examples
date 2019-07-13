package io.debezium.rsvp.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class FileHandler implements RsvpHandler {

    private Path rsvpDir;
    int count = 0;

    public FileHandler(String rsvpDir) {
        this.rsvpDir = new File(rsvpDir).toPath();
        if (!Files.exists(this.rsvpDir)) {
            try {
                Files.createDirectory(this.rsvpDir);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handle(String rsvp) {
        try {
            Files.write(rsvpDir.resolve(Instant.now().toString()), rsvp.getBytes(), StandardOpenOption.CREATE_NEW);
            count++;

            if (count % 10 == 0) {
                System.out.println("Created " + count + " files");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
