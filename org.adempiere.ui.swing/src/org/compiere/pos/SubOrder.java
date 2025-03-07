/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.compiere.pos;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.adempiere.plaf.AdempierePLAF;
import org.compiere.apps.ADialog;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerInfo;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCurrency;
import org.compiere.model.MOrder;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MUser;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;


/**
 *	Customer Sub Panel
 *	
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � Jorg Janke
 *  @version $Id: SubBPartner.java,v 1.1 2004/07/12 04:10:04 jjanke Exp $
 */
public class SubOrder extends PosSubPanel 
	implements ActionListener, FocusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2985454173868605178L;

	/**
	 * 	Constructor
	 *	@param posPanel POS Panel
	 */
	public SubOrder (PosBasePanel posPanel)
	{
		super (posPanel);
	}	//	PosSubCustomer
	
	private CButton 		f_history;
	private	PosTextField		f_name;
	private CButton 		f_bNew;
	//private CButton 		f_bSearch;
	private CComboBox<KeyNamePair>		f_location;
	private CComboBox<KeyNamePair>		f_user;
	private CButton 		f_process;
	private CButton 		f_print;
	private CTextField 		f_DocumentNo;
	private CButton 		f_logout;
	private JFormattedTextField f_net;
	private JFormattedTextField f_tax;
	private JFormattedTextField f_total;
	
	/**	The Business Partner		*/
	private MBPartner	m_bpartner;
	/**	Price List Version to use	*/
	private int			m_M_PriceList_Version_ID = 0;
	private CTextField f_currency = new CTextField();
	private CButton f_bEdit;
	private CButton f_bSettings;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(SubOrder.class);
	
	/**
	 * 	Initialize
	 */
	public void init()
	{
		//	Content
		MigLayout layout = new MigLayout("ins 0 0","[fill|fill|fill|fill]","[nogrid]unrel[||]");
		setLayout(layout);
		
		Font bigFont = AdempierePLAF.getFont_Field().deriveFont(20f);

		String buttonSize = "w 50!, h 50!,";
		// NEW
		f_bNew = createButtonAction("New", KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.F2));
		add (f_bNew, buttonSize);
			
		// HISTORY
		f_history = createButtonAction("History", null);
 		add (f_history, buttonSize); 

 		// EDIT
		f_bEdit = createButtonAction("Edit", null);
		add(f_bEdit, buttonSize);
 		f_bEdit.setEnabled(false);
		
		// CANCEL
		f_process = createButtonAction("Delete", null);
 		add (f_process, buttonSize);
 		f_process.setEnabled(false);
 		

 		//PRINT
		f_print = createButtonAction("Print", null);
 		add (f_print, buttonSize);
 		f_print.setEnabled(false);
 		
 		// Settings
		f_bSettings = createButtonAction("Preference", null);
 		add (f_bSettings, buttonSize);
 		
		//
		f_logout = createButtonAction ("Logout", null);
		add (f_logout, buttonSize + ", gapx 25, wrap");

		// SALES REP
		add(new CLabel(Msg.translate(Env.getCtx(), "Cashier")), "");
		CLabel cashiername = new CLabel((p_ctx.getProperty("#AD_User_Name")).toUpperCase());
		cashiername.setFont(bigFont);
		cashiername.setFontBold(true);
		cashiername.setForeground(Color.RED);
		add (cashiername, "growx, pushx");
		
		CLabel lNet = new CLabel (Msg.translate(Env.getCtx(), "SubTotal"));
		add(lNet, "");
		f_net = new JFormattedTextField(DisplayType.getNumberFormat(DisplayType.Amount));
		f_net.setHorizontalAlignment(JTextField.TRAILING);
		f_net.setEditable(false);  
		f_net.setFocusable(false);  
		lNet.setLabelFor(f_net);
		add(f_net, "wrap, growx, pushx");
		f_net.setValue (Env.ZERO);
		//

		// BPARTNER
//		f_bSearch = createButtonAction ("BPartner", KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.SHIFT_MASK+Event.CTRL_MASK));
//		add (f_bSearch,buttonSize + ", spany 2");

		// DOC NO
		add (new CLabel(Msg.getMsg(Env.getCtx(),"DocumentNo")), "");
		
		f_DocumentNo = new CTextField("");
		f_DocumentNo.setName("DocumentNo");
		f_DocumentNo.setEditable(false);
		add (f_DocumentNo, "growx, pushx");

		CLabel lTax = new CLabel (Msg.translate(Env.getCtx(), "TaxAmt"));
		add(lTax);
		f_tax = new JFormattedTextField(DisplayType.getNumberFormat(DisplayType.Amount));
		f_tax.setHorizontalAlignment(JTextField.TRAILING);
		f_tax.setEditable(false);
		f_tax.setFocusable(false);
		lTax.setLabelFor(f_tax);
		add(f_tax, "wrap, growx, pushx");
		f_tax.setValue (Env.ZERO);
		//
		
		/*
		f_location = new CComboBox<Object>();
		add (f_location, " wrap");
	*/
		
		// BP
		add(new CLabel(Msg.translate(Env.getCtx(), "Customer")), "");
		f_name =new PosTextField(Msg.translate(Env.getCtx(), "C_Partner_ID"), p_posPanel, p_pos.getOSK_KeyLayout_ID());		
		f_name.setEditable(true); //red1
		f_name.setName("BPartner");
		f_name.addActionListener(this);
		f_name.addFocusListener(this);
		add (f_name, " flowy, pushx, h 20!");

		//
		CLabel lTotal = new CLabel (Msg.translate(Env.getCtx(), "TOTAL"));
		lTotal.setFont(bigFont);
		add(lTotal, "");
		f_total = new JFormattedTextField(DisplayType.getNumberFormat(DisplayType.Amount));
		f_total.setHorizontalAlignment(JTextField.TRAILING);f_total.setFont(bigFont);f_total.setForeground(Color.BLUE);
		f_total.setEditable(false);
		f_total.setFocusable(false);
		f_total.setName("GrandTotal");
		lTotal.setLabelFor(f_total);
		add(f_total, "spanx 3, growx, pushx");
		f_total.setValue (Env.ZERO);
		/*
		//
		f_user = new CComboBox<Object>();
		add (f_user, "skip 1");
		*/
	}	//	init

	/**
	 * 	Dispose - Free Resources
	 */
	public void dispose()
	{
		if (f_name != null)
			f_name.removeFocusListener(this);
		f_name = null;
		removeAll();
		super.dispose();
	}	//	dispose

	
	/**************************************************************************
	 * 	Action Listener
	 *	@param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		String action = e.getActionCommand();
		if (action == null || action.length() == 0)
			return;
		if (log.isLoggable(Level.INFO)) log.info( "PosSubCustomer - actionPerformed: " + action);
		//	New
		if (action.equals("New"))
		{
			p_posPanel.newOrder(); //red1 New POS Order instead - B_Partner already has direct field
			return;
		}
		//	Register
		if (action.equals("History"))
		{
			PosQuery qt = new QueryTicket(p_posPanel);
			qt.setVisible(true);
			return;
		}
		else if (action.equals("Delete")) //red1 more apt description
			{
				deleteOrder();
				p_posPanel.m_order = null;
				p_posPanel.f_curLine.newLine();
				p_posPanel.f_curLine.f_name.requestFocusInWindow();
			}
		else if (action.equals("Preference"))
			{
			CashSubFunctions csf = new CashSubFunctions(p_posPanel);
			csf.setVisible(true);
			}
		else if (action.equals("Print"))
			printOrder();
		else if (action.equals("BPartner"))
		{
			PosQuery qt = new QueryBPartner(p_posPanel);
			qt.setVisible(true);
		}
		// Logout
		else if (action.equals("Logout"))
		{
			p_posPanel.dispose();
			return;
		}
		//	Name
		else if (e.getSource() == f_name)
			findBPartner();
		
		p_posPanel.updateInfo();
	}	//	actionPerformed

	/**
	 * 
	 */
	private void printOrder() {
		{
			if (isOrderFullyPaid())
			{
				updateOrder();
				printTicket();
				openCashDrawer();
			}
		}
	}



	/**
	 * 
	 */
	private void deleteOrder() {
		if ( p_posPanel != null && ADialog.ask(0, this, "Delete order?") )
			p_posPanel.m_order.deleteOrder();
		// p_posPanel.newOrder();

	}

	/**
	 * 	Focus Gained
	 *	@param e
	 */
	public void focusGained (FocusEvent e)
	{
	}	//	focusGained

	/**
	 * 	Focus Lost
	 *	@param e
	 */
	public void focusLost (FocusEvent e)
	{
		if (e.isTemporary())
			return;
		if (log.isLoggable(Level.INFO)) log.info(e.toString());
		findBPartner();
	}	//	focusLost

	
	/**
	 * 	Find/Set BPartner
	 */
	private void findBPartner()
	{
		
		String query = f_name.getText();
		
		if (query == null || query.length() == 0)
			return;
		
		// unchanged
		if ( m_bpartner != null && m_bpartner.getName().equals(query))
			return;
		
		query = query.toUpperCase();
		//	Test Number
		boolean allNumber = true;
		boolean noNumber = true;
		char[] qq = query.toCharArray();
		for (int i = 0; i < qq.length; i++)
		{
			if (Character.isDigit(qq[i]))
			{
				noNumber = false;
				break;
			}
		}
		try
		{
			Integer.parseInt(query);
		}
		catch (Exception e)
		{
			allNumber = false;
		}
		String Value = query;
		String Name = (allNumber ? null : query);
		String EMail = (query.indexOf('@') != -1 ? query : null); 
		String Phone = (noNumber ? null : query);
		String City = null;
		//
		//TODO: contact have been remove from rv_bpartner
		MBPartnerInfo[] results = MBPartnerInfo.find(p_ctx, Value, Name, 
			/*Contact, */null, EMail, Phone, City);
		
		//	Set Result
		if (results.length == 0)
		{
			setC_BPartner_ID(0);
		}
		else if (results.length == 1)
		{
			setC_BPartner_ID(results[0].getC_BPartner_ID());
			f_name.setText(results[0].getName());
		}
		else	//	more than one
		{
			QueryBPartner qt = new QueryBPartner(p_posPanel);
			qt.setResults (results);
			qt.setVisible(true);
		}
	}	//	findBPartner
	
	
	/**************************************************************************
	 * 	Set BPartner
	 *	@param C_BPartner_ID id
	 */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (log.isLoggable(Level.FINE)) log.fine( "PosSubCustomer.setC_BPartner_ID=" + C_BPartner_ID);
		if (C_BPartner_ID == 0)
			m_bpartner = null;
		else
		{
			m_bpartner = new MBPartner(p_ctx, C_BPartner_ID, null);
			if (m_bpartner.get_ID() == 0)
				m_bpartner = null;
		}
		
		//	Set Info
		if (m_bpartner != null)
		{
			f_name.setText(m_bpartner.getName());
		}
		else
		{
			f_name.setText(null);
		}
		//	Sets Currency
		m_M_PriceList_Version_ID = 0;
		getM_PriceList_Version_ID();
		//fillCombos();
		if ( p_posPanel.m_order != null && m_bpartner != null )
			p_posPanel.m_order.setBPartner(m_bpartner);  //added by ConSerTi to update the client in the request
	}	//	setC_BPartner_ID

	/**
	 * 	Fill Combos (Location, User)
	 */
	@SuppressWarnings("unused")
	private void fillCombos()
	{
		Vector<KeyNamePair> locationVector = new Vector<KeyNamePair>();
		if (m_bpartner != null)
		{
			MBPartnerLocation[] locations = m_bpartner.getLocations(false);
			for (int i = 0; i < locations.length; i++)
				locationVector.add(locations[i].getKeyNamePair());
		}
		DefaultComboBoxModel<KeyNamePair> locationModel = new DefaultComboBoxModel<KeyNamePair>(locationVector); 
		f_location.setModel(locationModel);
		//
		Vector<KeyNamePair> userVector = new Vector<KeyNamePair>();
		if (m_bpartner != null)
		{
			MUser[] users = m_bpartner.getContacts(false);
			for (int i = 0; i < users.length; i++)
				userVector.add(users[i].getKeyNamePair());
		}
		DefaultComboBoxModel<KeyNamePair> userModel = new DefaultComboBoxModel<KeyNamePair>(userVector); 
		f_user.setModel(userModel);
	}	//	fillCombos
	
	
	/**
	 * 	Get BPartner
	 *	@return C_BPartner_ID
	 */
	public int getC_BPartner_ID ()
	{
		if (m_bpartner != null)
			return m_bpartner.getC_BPartner_ID();
		return 0;
	}	//	getC_BPartner_ID

	/**
	 * 	Get BPartner
	 *	@return BPartner
	 */
	public MBPartner getBPartner ()
	{
		return m_bpartner;
	}	//	getBPartner
	
	/**
	 * 	Get BPartner Location
	 *	@return C_BPartner_Location_ID
	 */
	public int getC_BPartner_Location_ID ()
	{
		if (m_bpartner != null)
		{
			KeyNamePair pp = (KeyNamePair)f_location.getSelectedItem();
			if (pp != null)
				return pp.getKey();
		}
		return 0;
	}	//	getC_BPartner_Location_ID
	
	/**
	 * 	Get BPartner Contact
	 *	@return AD_User_ID
	 */
	public int getAD_User_ID ()
	{
		if (m_bpartner != null)
		{
			KeyNamePair pp = (KeyNamePair)f_user.getSelectedItem();
			if (pp != null)
				return pp.getKey();
		}
		return 0;
	}	//	getC_BPartner_Location_ID

	/**
	 * 	Get M_PriceList_Version_ID.
	 * 	Set Currency
	 *	@return plv
	 */
	public int getM_PriceList_Version_ID()
	{
		if (m_M_PriceList_Version_ID == 0)
		{
			int M_PriceList_ID = p_pos.getM_PriceList_ID();
			if (m_bpartner != null && m_bpartner.getM_PriceList_ID() != 0)
				M_PriceList_ID = m_bpartner.getM_PriceList_ID();
			//
			MPriceList pl = MPriceList.get(p_ctx, M_PriceList_ID, null);
			setCurrency(MCurrency.getISO_Code(p_ctx, pl.getC_Currency_ID()));
			f_name.setToolTipText(pl.getName());
			//
			MPriceListVersion plv = pl.getPriceListVersion (p_posPanel.getToday());
			if (plv != null && plv.getM_PriceList_Version_ID() != 0)
				m_M_PriceList_Version_ID = plv.getM_PriceList_Version_ID();
		}
		return m_M_PriceList_Version_ID;
	}	//	getM_PriceList_Version_ID
	

	/***************************************************************************
	 * Set Currency
	 * 
	 * @param currency
	 *            currency
	 */
	public void setCurrency(String currency) {
		if (currency == null)
			f_currency.setText("---");
		else
			f_currency.setText(currency);
	} //	setCurrency
	

	/**
	 * Is order fully pay ?
	 * Calculates if the given money is sufficient to pay the order
	 * 
	 * @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	 */
	public boolean isOrderFullyPaid()
	{
		/*TODO
		BigDecimal given = new BigDecimal(f_cashGiven.getValue().toString());
		boolean paid = false;
		if (p_posPanel != null && p_posPanel.f_curLine != null)
		{
			MOrder order = p_posPanel.f_curLine.getOrder();
			BigDecimal total = Env.ZERO;
			if (order != null)
				total = order.getGrandTotal();
			paid = given.doubleValue() >= total.doubleValue();
		}
		return paid;
		*/
		return true;
	}
	
	/**
	 * 	Display cash return
	 *  Display the difference between tender amount and bill amount
	 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	 */
	public void updateOrder()
	{
		if (p_posPanel != null )
		{
			MOrder order = p_posPanel.m_order;
			if (order != null)
			{
  				f_DocumentNo.setText(order.getDocumentNo());
  				setC_BPartner_ID(order.getC_BPartner_ID());
  				f_bNew.setEnabled(order.getLines().length != 0);
  				f_bEdit.setEnabled(true);
  				f_process.setEnabled(true);
  				f_print.setEnabled(order.isProcessed());
  				f_cashPayment.setEnabled(order.getLines().length != 0);
			}
			else
			{
				f_DocumentNo.setText(null);
				setC_BPartner_ID(0);
				f_bNew.setEnabled(true);
				f_bEdit.setEnabled(false);
				f_process.setEnabled(false);
				f_print.setEnabled(false);
				f_cashPayment.setEnabled(false);
			}
			
		}
	}	

	/**
	 * 	Abrir caja
	 *  Abre la caja registradora
	 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	 */
	public void openCashDrawer()
	{
		String port = "/dev/lp";
		
		byte data[] = new byte[] {0x1B, 0x40, 0x1C};
		try {  
            FileOutputStream m_out = null;
			if (m_out == null) {
                m_out = new FileOutputStream(port);  // No poner append = true.
            }
            m_out.write(data);
        } catch (IOException e) {
        }  
	}	

	/**
	 * 	Set Sums from Table
	 */
	void setSums(PosOrderModel order)
	{
		int noLines = p_posPanel.f_curLine.m_table.getRowCount();
		if (order == null || noLines == 0)
		{
			f_net.setValue(Env.ZERO);
			f_total.setValue(Env.ZERO);
			f_tax.setValue(Env.ZERO);
		}
		else
		{
			// order.getMOrder().prepareIt();
			f_net.setValue(order.getSubtotal());
			f_total.setValue(order.getGrandTotal());
			f_tax.setValue(order.getTaxAmt());

		}
	}	//	setSums
}	//	PosSubCustomer
