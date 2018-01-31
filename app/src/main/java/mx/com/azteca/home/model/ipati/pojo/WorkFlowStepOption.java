package mx.com.azteca.home.model.ipati.pojo;


public class WorkFlowStepOption {

    public int IdDocumento;
    public int Version;
    public int IdWorkFlowStep;
    public int IdWorkFlowOption;
    public String Tag;
    public String Text;

    private WorkFlowStep Option;

    public WorkFlowStepOption() {
        this.Option = new WorkFlowStep();
    }

    public WorkFlowStep getOption() {
        return Option;
    }

    public void setOption(WorkFlowStep option) {
        this.Option = option;
    }
}
