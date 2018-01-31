package mx.com.azteca.home.model.ipati.pojo;


public class EventLog {

    public enum EventType {
        INFORMATION(1),
        ERROR(2),
        WARNING(3);

        public int numType;

        EventType(int numType) {
            this.numType = numType;
        }

    }

    public long Fecha;
    public int Type;
    public String Info;

}
