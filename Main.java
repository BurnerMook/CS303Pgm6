    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        int numberOfLines = 0;

        System.out.println("GROCERY STORE SIMULATION");
        System.out.println("------------------------");

        while (numberOfLines < 1 || numberOfLines > 10) {

            System.out.print(
                "Enter the number of checkout lines (1-10): "
            );

            if (input.hasNextInt()) {

                numberOfLines = input.nextInt();

                if (numberOfLines < 1 || numberOfLines > 10) {
                    System.out.println(
                        "Please enter a number between 1 and 10."
                    );
                }
            }
            else {
                System.out.println("Please enter a whole number.");
                input.next();
            }
        }

        StoreSim simulation =
            new StoreSim(numberOfLines);

        System.out.println();
        System.out.println("Running simulation...");

        simulation.runSimulation();
        simulation.printReport();

        input.close();
    }