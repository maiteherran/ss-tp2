package ss.g7q12020;

import org.apache.commons.cli.*;

public class ProgramParameters {
    private long N;
    private long i;
    private Double L;
    private Double Rc;
    private Double noise;

    public void parse(String[] commandLineArgs) {
        Options options = new Options();

        Option nParticles = Option.builder().argName("N")
                .valueSeparator('=').desc("Number of particles. Integer.")
                .hasArg().required().longOpt("N").build();
        //new Option("N", true, "Number of particles. Integer.");
        //nParticles.setRequired(true);
        options.addOption(nParticles);

        Option areaSide = Option.builder().argName("L").valueSeparator('=')
                .desc("Length of area side. Double.").hasArg().required()
                .longOpt("L").build();
        //new Option("L",  true, "Length of area side. Double.");
        //areaSide.setRequired(true);
        options.addOption(areaSide);

        Option cutoff = Option.builder().argName("Rc")
                .desc("Interaction radius. Double.").hasArg()
                .valueSeparator('=').longOpt("Rc").build();
        //new Option("Rc",  true, "Interaction radius. Double.");
        //cutoff.setRequired(true);
        options.addOption(cutoff);

        Option n = Option.builder().argName("noise").required().hasArg()
                .desc("Noise. Double.")
                .valueSeparator('=').longOpt("noise").build();
        //new Option("noise", true, "Noise. Double.");
        //n.setRequired(true);
        options.addOption(n);

        Option it = Option.builder().argName("i").valueSeparator('=')
                .hasArg().desc("Iterations. Integer")
                .longOpt("i").build();
        //new Option("i",  true, "Iterations. Integer");
        //it.setRequired(true);
        options.addOption(it);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, commandLineArgs);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Particles generator", options);
            System.exit(1);
        }

        N = Long.parseLong(cmd.getOptionValue("N"));
        L = Double.parseDouble(cmd.getOptionValue("L"));
        Rc = Double.parseDouble(cmd.getOptionValue("Rc"));
        noise = Double.parseDouble(cmd.getOptionValue("noise"));
        i = Long.parseLong(cmd.getOptionValue("i"));

    }

    public ProgramParameters(long n, long i, Double l, Double rc, Double noise) {
        N = n;
        this.i = i;
        L = l;
        Rc = rc;
        this.noise = noise;
    }

    public ProgramParameters() {
        i = 0;
        N = 0;
        L = 0.0;
        Rc = 0.0;
        noise = 0.0;
    }

    public long getN() {
        return N;
    }

    public Double getL() {
        return L;
    }

    public Double getRc() {
        return Rc;
    }

    public Double getNoise() {
        return noise;
    }

    public long getI() {
        return i;
    }

    public void setN(long n) {
        N = n;
    }

    public void setI(long i) {
        this.i = i;
    }

    public void setL(Double l) {
        L = l;
    }

    public void setRc(Double rc) {
        Rc = rc;
    }

    public void setNoise(Double noise) {
        this.noise = noise;
    }

    @Override
    public String toString() {
        return "ProgramParameters{" +
                "N=" + N +
                ", i=" + i +
                ", L=" + L +
                ", Rc=" + Rc +
                ", noise=" + noise +
                '}';
    }
}
