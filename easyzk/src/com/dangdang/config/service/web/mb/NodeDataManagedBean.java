package com.dangdang.config.service.web.mb;

import com.dangdang.config.business.INodeBusiness;
import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItemVO;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="nodeDataMB")
@ViewScoped
public class NodeDataManagedBean
  implements Serializable, IObserver
{
  private static final long serialVersionUID = 1L;

  @ManagedProperty("#{nodeService}")
  private INodeService nodeService;

  @ManagedProperty("#{nodeBusiness}")
  private INodeBusiness nodeBusiness;

  @ManagedProperty("#{nodeAuthMB}")
  private NodeAuthManagedBean nodeAuth;

  @ManagedProperty("#{versionMB}")
  private VersionManagedBean versionMB;
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeDataManagedBean.class);
  private String selectedNode;
  private List<PropertyItemVO> nodeProps;
  private InputText newPropName;
  private InputText newPropValue;
  private InputText newCommentValue;

  public void setNodeService(INodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public void setNodeBusiness(INodeBusiness nodeBusiness)
  {
    this.nodeBusiness = nodeBusiness;
  }

  public void setNodeAuth(NodeAuthManagedBean nodeAuth)
  {
    this.nodeAuth = nodeAuth;
  }

  public void setVersionMB(VersionManagedBean versionMB)
  {
    this.versionMB = versionMB;
  }

  @PostConstruct
  private void init() {
    this.nodeAuth.register(this);
  }

  public String getSelectedNode()
  {
    return this.selectedNode;
  }

  public void setSelectedNode(String selectedNode) {
    this.selectedNode = selectedNode;
  }

  public void refreshNodeProperties(String selectedNode)
  {
    this.selectedNode = selectedNode;
    LOGGER.info("Find properties of node: [{}].", selectedNode);

    this.nodeProps = this.nodeBusiness.findPropertyItems(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion(), selectedNode);
  }

  private String getSelectedNodePath()
  {
    if (Strings.isNullOrEmpty(this.selectedNode))
      return null;
    String authedNode = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion());
    return ZKPaths.makePath(authedNode, this.selectedNode);
  }

  private String getSelectedNodeCommentPath()
  {
    if (Strings.isNullOrEmpty(this.selectedNode))
      return null;
    String authedNode = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion() + "$");
    return ZKPaths.makePath(authedNode, this.selectedNode);
  }

  private String getPropertyNodePath(String propertyName)
  {
    return ZKPaths.makePath(getSelectedNodePath(), propertyName);
  }

  private String getPropertyCommentPath(String propertyName)
  {
    return ZKPaths.makePath(getSelectedNodeCommentPath(), propertyName);
  }

  public void setNodeProps(List<PropertyItemVO> nodeProps)
  {
    this.nodeProps = nodeProps;
  }

  public List<PropertyItemVO> getNodeProps() {
    return this.nodeProps;
  }

  public void onPropertyEdit(RowEditEvent event)
  {
    if (!checkPropertyGroupCheckedStatus()) {
      return;
    }

    PropertyItemVO selectedItem = (PropertyItemVO)event.getObject();

    LOGGER.info("Update property with : {}.", selectedItem);

    String name = selectedItem.getName();
    String oriName = selectedItem.getOriName();

    boolean suc = false;
    String fullPropPath = getPropertyNodePath(name);
    String fullCommentPath = getPropertyCommentPath(name);
    if (name.equals(oriName)) {
      suc = this.nodeService.updateProperty(fullPropPath, selectedItem.getValue());
      this.nodeService.updateProperty(fullCommentPath, selectedItem.getComment());
    } else {
      this.nodeService.deleteProperty(getPropertyNodePath(oriName));
      suc = this.nodeService.createProperty(fullPropPath, selectedItem.getValue());

      this.nodeService.deleteProperty(fullCommentPath);
      this.nodeService.createProperty(fullCommentPath, selectedItem.getComment());
    }

    if (suc)
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property Saved suc.", name));
    else
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Property Saved failed.", name));
  }

  public InputText getNewPropName()
  {
    return this.newPropName;
  }

  public void setNewPropName(InputText newPropName) {
    this.newPropName = newPropName;
  }

  public InputText getNewPropValue() {
    return this.newPropValue;
  }

  public void setNewPropValue(InputText newPropValue) {
    this.newPropValue = newPropValue;
  }

  public InputText getNewCommentValue() {
    return this.newCommentValue;
  }

  public void setNewCommentValue(InputText newCommentValue) {
    this.newCommentValue = newCommentValue;
  }

  public void createProperty()
  {
    if (!checkPropertyGroupCheckedStatus()) {
      return;
    }

    String propName = (String)this.newPropName.getValue();
    String propValue = (String)this.newPropValue.getValue();
    String propComment = (String)this.newCommentValue.getValue();

    String propPath = getPropertyNodePath(propName);
    LOGGER.info("Create property: Path[{}], Value[{}]", propPath, propValue);
    boolean created = this.nodeService.createProperty(propPath, propValue);
    if (created) {
      if (!Strings.isNullOrEmpty(propComment)) {
        this.nodeService.createProperty(getPropertyCommentPath(propName), propComment);
      }
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property created.", propPath));
      refreshNodeProperties(this.selectedNode);

      this.newPropName.setValue(null);
      this.newPropValue.setValue(null);
      this.newCommentValue.setValue(null);
    } else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Property creation failed.", propPath));
    }
  }

  public void deleteProperty(String propName)
  {
    if (!checkPropertyGroupCheckedStatus()) {
      return;
    }

    String propPath = getPropertyNodePath(propName);
    LOGGER.info("Delete property: Path[{}]", propPath);

    this.nodeService.deleteProperty(propPath);
    this.nodeService.deleteProperty(getPropertyCommentPath(propName));

    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property deleted.", propPath));
    refreshNodeProperties(this.selectedNode);
  }

  private boolean checkPropertyGroupCheckedStatus()
  {
    boolean notChecked = Strings.isNullOrEmpty(this.selectedNode);
    if (notChecked) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select a property group first."));
    }
    return !notChecked;
  }

  public void notified(String data, String value)
  {
    this.nodeProps = null;
  }
}