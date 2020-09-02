package pl.mswierczewski.socialwall.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignOutRequest {

    @JsonProperty
    private boolean onAllDevices;

    public boolean isOnAllDevices() {
        return onAllDevices;
    }

    public void setOnAllDevices(boolean onAllDevices) {
        this.onAllDevices = onAllDevices;
    }

    public boolean getOnAllDevices() {
        return onAllDevices;
    }

}
