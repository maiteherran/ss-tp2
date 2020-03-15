package ss.g7q12020;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReturnFileUtils {

    private static void writeReturnFile(Map<Long, Set<Long>> idNeighborsMap) throws IOException {
        Files.write(Paths.get("./returnedNeighbors.txt"), () -> idNeighborsMap.entrySet().stream()
                .<CharSequence>map(idMap -> idMap.getKey().toString() + ' ' + idMap.getValue()
                        .stream().map(Object::toString).collect(Collectors.joining(" ")))
                .iterator());
    }

    private static void generateOvitofile(List<Particle> particleList, long targetParticleId, Set<Long> targetNeighboursIds) throws IOException {
        BufferedWriter xyzFile = new BufferedWriter(new FileWriter("./ovito"));
        xyzFile.write(String.valueOf(particleList.size()));
        xyzFile.newLine();
        xyzFile.newLine();
        String line;
        for (Particle particle : particleList) {
            if (particle.getId() == targetParticleId) {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "255" + " " + "100" + " " + "100";
            } else if (targetNeighboursIds.contains(particle.getId())) {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "50" + " " + "100" + " " + "100";
            } else {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "150" + " " + "100" + " " + "100";
            }
            xyzFile.write(line);
            xyzFile.newLine();
        }
        xyzFile.close();
    }

}
