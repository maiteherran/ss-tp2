package ss.g7q12020;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static  ProgramParameters parameters;
    private static BufferedWriter file;
    private static List<Particle> particlesDisposition;
    private final static double defaultSpeedModule = 0.03;


    public static void main(String[] args) {
        parameters = new ProgramParameters();
        parameters.parse(args);

        long startTime = System.nanoTime();
        try {
            generateAutomation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long finishTime = System.nanoTime();
        System.out.println("Tiempo de ejecución: "+ 1E-9*(System.nanoTime() - startTime)+ " segs");
    }

    private static void generateAutomation() throws IOException {
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

}
