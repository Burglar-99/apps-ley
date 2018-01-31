package mx.com.azteca.home.model.ipati.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkFlowStep {

    public int ID;
    public int IdWorkflowStep;
    public String Tag;
    public String ActionText;
    public String StatusText;
    public boolean Optional;
    public int Orden;
    public int Icon;
    public int IdFormulario;
    public double Duracion;
    public boolean Offline;
    public boolean IsStart;
    public boolean IsEnd;

    public int NextStep;
    public int HasOptions;

    public int IdDocumento;
    public int Version;

    private WorkFlowStep WorkFlowNext;
    private List<WorkFlowStepOption> Options;
    private List<DocumentoAsignado> DocsAsignados;
    private Formulario formulario;

    public WorkFlowStep() {
        this.Options = new ArrayList<>();
        this.DocsAsignados = new ArrayList<>();
    }

    public List<WorkFlowStepOption> getOptions() {
        return Options;
    }

    public void setWorkFlowNext(WorkFlowStep workFlowNext) {
        this.WorkFlowNext = workFlowNext;
    }

    public WorkFlowStep getWorkFlowNext() {
        return WorkFlowNext;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public List<DocumentoAsignado> getDocsAsignados() {
        return DocsAsignados;
    }

    @Override
    public String toString() {
        return this.ActionText;
    }
}
