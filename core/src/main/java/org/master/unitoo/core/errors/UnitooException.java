package org.master.unitoo.core.errors;

import java.io.IOException;
import org.master.unitoo.core.api.IErrorType;

/**
 *
 * @author Andrey
 */
public class UnitooException extends IOException {

    private Object[] params;
    private final IErrorType type;

    public UnitooException(IErrorType type) {
        this.type = type;
    }

    public UnitooException(IErrorType type, Object... params) {
        this.params = params;
        this.type = type;
    }

    public UnitooException(IErrorType type, Throwable cause, Object... params) {
        super(cause);
        this.params = params;
        this.type = type;
    }

    public UnitooException(IErrorType type, String message, Object... params) {
        super(message);
        this.params = params;
        this.type = type;
    }

    public UnitooException(IErrorType type, String message, Throwable cause, Object... params) {
        super(message, cause);
        this.params = params;
        this.type = type;
    }

    public void params(Object... params) {
        this.params = params;
    }

    public Object[] params() {
        return params;
    }

    @Override
    public String getMessage() {
        if (params != null) {
            try {
                return String.format(super.getMessage(), params);
            } catch (Throwable t) {
                return "INVALID FORMAT(" + t.getMessage() + "): " + super.getMessage();
            }
        } else {
            return super.getMessage();
        }
    }

    public IErrorType type() {
        return type;
    }

    public String code() {
        return type == null ? SystemErrorCodes.UTS_SystemException.code() : type.code();
    }
}
