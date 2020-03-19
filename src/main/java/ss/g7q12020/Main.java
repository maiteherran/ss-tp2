package ss.g7q12020;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static ProgramParameters parameters;
    private static BufferedWriter vaFile;
    private static List<Particle> particlesDisposition;
    private final static double defaultSpeedModule = 0.03;


    public static void main(String[] args) {
        parameters = new ProgramParameters();
        parameters.parse(args);
        System.out.println("Empezando el analisis de las velocidades");
        //long startTime = System.nanoTime();
        calculateToItGraph();
    }

    private static void generateAutomation() throws IOException {
        //Creamos el archivo de output
        BufferedWriter file = new BufferedWriter(new FileWriter("output.txt"));

        ParticlesGenerator initialParticlesGenerator = new ParticlesGenerator(parameters.getN(), parameters.getL());
        particlesDisposition = initialParticlesGenerator.generate();

        writeOnFileVa(0, calculateVa());
        //vamos moviendo a las particulas
        for (int i = 1; i < parameters.getI(); i++) {
            moveParticles();
            writeOnFileVa(i, calculateVa());

        }
    }

    private static void moveParticles() {
        //Calculamos los vecinos de cada partícula
        final Map<Long, Set<Long>> neighbors = NeighborAlgorithm.findNeighbors(particlesDisposition, true, parameters.getRc(), parameters.getL());
        for (Particle p : particlesDisposition) {
            p.move(parameters.getL(), 1);
            p.changeAngle(calculateAngle(p.id, neighbors.get(p.id), parameters.getNoise()));
        }
    }

    private static double calculateAngle(final long target, final Set<Long> neighbors, final double noise) {
        neighbors.add(target); //la partícula target cuenta en el promedio
        List<Particle> particles = particlesDisposition
                .stream().filter(p -> neighbors
                        .stream().anyMatch(x -> x.equals(p.getId())))
                .collect(Collectors.toList());
        double sin = 0.0;
        double cos = 0.0;
        for (Particle p : particles) {
            sin += Math.sin(p.getAngle());
            cos += Math.cos(p.getAngle());
        }
        sin /= particles.size();
        cos /= particles.size();

        return Math.atan2(sin, cos) + ((Math.random() - 0.50) * noise);
    }

    private static double calculateVa() {
        double vx = 0.0;
        double vy = 0.0;
        for (Particle p : particlesDisposition) {
            vx += p.getVx();
            vy += p.getVy();
        }
        return Math.hypot(vx, vy) / (defaultSpeedModule * parameters.getN());
    }

    private static void writeOnFileVa(long time, double va) throws IOException {
        vaFile.write(String.valueOf(time) + ' ' + va);
        vaFile.newLine();
    }

    private static void calculateVaNoise() {
        try {
            for (int k = 1; k <= 5; k++) {
                System.out.println(k);
                vaFile = new BufferedWriter(new FileWriter("vaOutputNoise" + k + ".txt"));
                parameters.setI(1000);
                double density = 4;
                int[] nList = {50, 100, 500};
                for (int j : nList) {
                    parameters.setN(j);
                    System.out.println("N" + parameters.getN());
                    parameters.setL(Math.sqrt(parameters.getN() / density));
                    parameters.setRc(1.0);
                    for (double i = 0; i <= 5; i += 0.5) {
                        parameters.setNoise(i);
                        System.out.println("Noise: " + parameters.getNoise());
                        vaFile.write("N: " + parameters.getN() +
                                " ruido: " + parameters.getNoise());
                        vaFile.newLine();
                        generateAutomation();
                    }
                }
                vaFile.close();
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

    private static void calculateToItGraph() {
        try {
            vaFile = new BufferedWriter(new FileWriter("vaOutputIter.txt"));
            parameters.setI(600);
            double density = 4;
            int[] nList = {50, 100};
            for (int j : nList) {
                parameters.setN(j);
                System.out.println("N" + parameters.getN());
                parameters.setL(Math.sqrt(parameters.getN() / density));
                parameters.setRc(1.0);
                for (double i = 0; i <= 4; i += 0.5) {
                    parameters.setNoise(i);
                    System.out.println("Noise: " + parameters.getNoise());
                    vaFile.write("N: " + parameters.getN() +
                            " ruido: " + parameters.getNoise());
                    vaFile.newLine();
                    generateAutomation();
                }
            }
            vaFile.close();

        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

    private static void calculateVaDensity() {
        try {
            for (int k = 1; k <= 5; k++) {
                vaFile = new BufferedWriter(new FileWriter("vaOutputDensity" + k + ".txt"));
                parameters.setI(500);
                parameters.setNoise(2.0);
                parameters.setL(10.0);
                parameters.setRc(1.0);
                for (int i = 1; i <= 10; i++) {//busco primero los puntos de va que se estabiliza con mismo ruido
                    parameters.setN(100 * i);
                    Double density = parameters.getN() / (parameters.getL() * parameters.getL());
                    System.out.println(density);
                    vaFile.write("N: " + parameters.getN() +
                            " densidad: " + density);
                    vaFile.newLine();
                    System.out.println(parameters);
                    generateAutomation();
                }
                vaFile.close();
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

}
