package ss.g7q12020;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static ProgramParameters parameters;

    public static void main(String[] args) {
        parameters = new ProgramParameters();
        parameters.parse(args);

        long startTime = System.nanoTime();
        generateAutomation();
    }

    private static void generateAutomation() {
        ParticlesGenerator initialParticlesGenerator = new ParticlesGenerator(parameters.getN(), parameters.getL());
        final List<Particle> initialParticlesDisposition = initialParticlesGenerator.generate();


    }

}
