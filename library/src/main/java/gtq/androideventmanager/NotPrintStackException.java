/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager;

/**
 *
 */
public class NotPrintStackException extends Exception {

    public NotPrintStackException() {
        super();
    }

    public NotPrintStackException(String error) {
        super(error);
    }

    public NotPrintStackException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotPrintStackException(Throwable throwable) {
        super(throwable);
    }
}
