package burrow.server;

import java.beans.JavaBean;

@JavaBean
public class BurrowResponse {
    private String message;
    private Integer exitCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(final Integer exitCode) {
        this.exitCode = exitCode;
    }
}
