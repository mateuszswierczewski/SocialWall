package pl.mswierczewski.socialwall.dtos;

public class SignOutRequest {

    private boolean signOutOnAllDevices;

    public SignOutRequest(boolean signOutOnAllDevices) {
        this.signOutOnAllDevices = signOutOnAllDevices;
    }

    public boolean isSignOutOnAllDevices() {
        return signOutOnAllDevices;
    }

    public void setSignOutOnAllDevices(boolean signOutOnAllDevices) {
        this.signOutOnAllDevices = signOutOnAllDevices;
    }
}
