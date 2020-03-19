package ss.g7q12020;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static  ProgramParameters parameters;
    private static BufferedWriter file;
    private static BufferedWriter vaFile;
    private static List<Particle> particlesDisposition;
    private final static double defaultSpeedModule = 0.03;


    public static void main(String[] args) {
        parameters = new ProgramParameters();
        parameters.parse(args);

        long startTime = System.nanoTime();
        try {
            generateAutomation(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long finishTime = System.nanoTime();
        System.out.println("Tiempo de ejecución: "+ 1E-9*(System.nanoTime() - startTime)+ " segs");
    }

    private static void generateAutomation(boolean finalVa) throws IOException {
        //Creamos el archivo de output
        file= new BufferedWriter(new FileWriter("output.txt"));

        //Creamos la disposición inicial de particulas
        ParticlesGenerator initialParticlesGenerator = new ParticlesGenerator(parameters.getN(), parameters.getL());
        particlesDisposition = initialParticlesGenerator.generate();
        writeOnFile(0);
        System.out.println("Va inicial: " + calculateVa());

        //vamos moviendo a las particulas
        for (int i = 1; i < parameters.getI() ; i++) {
             moveParticles();
             writeOnFile(i);
             if(!finalVa){
                 writeOnFileVa(i, calculateVa());
             }
        }

        //Cerramos el archivo output
        file.close();
        System.out.println("Va final: " + calculateVa());
    }

    private static void moveParticles () {
        //Calculamos los vecinos de cada partícula
        final Map<Long, Set<Long>> neighbors = NeighborAlgorithm.findNeighbors(particlesDisposition, true, parameters.getRc(), parameters.getL());
        for (int i = 0; i < particlesDisposition.size() ; i ++) {
            Particle p = particlesDisposition.get(i);
            p.move( parameters.getL(), 1);
            p.changeAngle(calculateAngle(p.id, neighbors.get(p.id), parameters.getNoise()));
        }
    }

    private static double calculateAngle(final long target, final Set<Long> neighbors, final double noise) {
        neighbors.add(target); //la partícula target cuenta en el promedio
        List <Particle> particles = particlesDisposition.stream().filter(p -> neighbors.stream().anyMatch(x -> x.equals(p.getId()))).collect(Collectors.toList());
        double sin = 0.0;
        double cos = 0.0;
        for (Particle p : particles) {
            sin += Math.sin(p.getAngle());
            cos += Math.cos(p.getAngle());
        }
        sin /= particles.size();
        cos /= particles.size();

        return Math.atan2(sin, cos) + ((Math.random() - 0.50)* noise);
    }

    private static double calculateVa() {
        double vx = 0.0;
        double vy = 0.0;
        for(Particle p : particlesDisposition) {
            vx += p.getVx();
            vy += p.getVy();
        }
        return Math.hypot(vx, vy) / (defaultSpeedModule * parameters.getN());
    }



    private static void writeOnFile(long time) throws IOException {
        file.write(String.valueOf(parameters.getN()));
        file.newLine();

        file.write(String.valueOf(time));
        file.newLine();

        for (Particle particle : particlesDisposition) {
            file.write(
                    particle.getX() + " " +
                            particle.getY() + " " +
                            particle.getVx() + " " +
                            particle.getVy()
            );
            file.newLine();
        }
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
                        generateAutomation(false);
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
                    generateAutomation(false);
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
                    generateAutomation(false);
                }
                vaFile.close();
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

}
