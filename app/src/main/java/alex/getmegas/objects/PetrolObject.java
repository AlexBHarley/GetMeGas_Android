package alex.getmegas.objects;

/**
 * Created by alex on 5/09/15.
 */
public class PetrolObject {
    private PetrolStation station;
    private Double distance;

    public PetrolObject(Double distance, PetrolStation station){
        this.station = station;
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public PetrolStation getStation() {
        return station;
    }
}
