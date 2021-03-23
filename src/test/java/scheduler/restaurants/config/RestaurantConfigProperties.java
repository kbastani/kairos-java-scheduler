package scheduler.restaurants.config;

public class RestaurantConfigProperties {

    private Integer numChefs = 1;
    private Long newOrderRate = 1000L;

    public RestaurantConfigProperties() {
    }

    public RestaurantConfigProperties(Integer numChefs) {
        this.numChefs = numChefs;
    }

    public RestaurantConfigProperties(Integer numChefs, Long newOrderRate) {
        this.numChefs = numChefs;
        this.newOrderRate = newOrderRate;
    }

    public Integer getNumChefs() {
        return numChefs;
    }

    public void setNumChefs(Integer numChefs) {
        this.numChefs = numChefs;
    }

    public Long getNewOrderRate() {
        return newOrderRate;
    }

    public void setNewOrderRate(Long newOrderRate) {
        this.newOrderRate = newOrderRate;
    }
}
