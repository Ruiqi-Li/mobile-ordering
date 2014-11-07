library app_bootstrap;

import 'package:polymer/polymer.dart';

import 'package:core_elements/core_toolbar.dart' as i0;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_selection.dart' as i1;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_selector.dart' as i2;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_ripple.dart' as i3;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_tab.dart' as i4;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_tabs.dart' as i5;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_meta.dart' as i6;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_transition.dart' as i7;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_key_helper.dart' as i8;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_overlay_layer.dart' as i9;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_overlay.dart' as i10;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_transition_css.dart' as i11;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_media_query.dart' as i12;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_toast.dart' as i13;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_iconset.dart' as i14;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_icon.dart' as i15;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_focusable.dart' as i16;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_shadow.dart' as i17;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_button.dart' as i18;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_animated_pages.dart' as i19;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_pages.dart' as i20;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_iconset_svg.dart' as i21;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_field.dart' as i22;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_input.dart' as i23;
import 'package:polymer/src/build/log_injector.dart';
import 'elements/system_subpage.dart' as i24;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_icon_button.dart' as i25;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_fab.dart' as i26;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_radio_button.dart' as i27;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_radio_group.dart' as i28;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_dialog.dart' as i29;
import 'package:polymer/src/build/log_injector.dart';
import 'elements/product_subpage.dart' as i30;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_checkbox.dart' as i31;
import 'package:polymer/src/build/log_injector.dart';
import 'package:paper_elements/paper_dialog_transition.dart' as i32;
import 'package:polymer/src/build/log_injector.dart';
import 'elements/employee_subpage.dart' as i33;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_menu.dart' as i34;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_item.dart' as i35;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_collapse.dart' as i36;
import 'package:polymer/src/build/log_injector.dart';
import 'package:core_elements/core_submenu.dart' as i37;
import 'package:polymer/src/build/log_injector.dart';
import 'elements/customer_subpage.dart' as i38;
import 'package:polymer/src/build/log_injector.dart';
import 'elements/main_page.dart' as i39;
import 'package:polymer/src/build/log_injector.dart';
import 'mobileordering.html.0.dart' as i40;
import 'package:polymer/src/build/log_injector.dart';
import 'package:smoke/smoke.dart' show Declaration, PROPERTY, METHOD;
import 'package:smoke/static.dart' show useGeneratedCode, StaticConfiguration;
import 'elements/system_subpage.dart' as smoke_0;
import 'package:polymer/polymer.dart' as smoke_1;
import 'package:observe/src/metadata.dart' as smoke_2;
import 'elements/product_subpage.dart' as smoke_3;
import 'elements/employee_subpage.dart' as smoke_4;
import 'elements/customer_subpage.dart' as smoke_5;
import 'elements/main_page.dart' as smoke_6;
abstract class _M0 {} // PolymerElement & ChangeNotifier

void main() {
  useGeneratedCode(new StaticConfiguration(
      checkedMode: false,
      getters: {
        #$: (o) => o.$,
        #actioning: (o) => o.actioning,
        #activate: (o) => o.activate,
        #active: (o) => o.active,
        #autoCloseDisabled: (o) => o.autoCloseDisabled,
        #autofocus: (o) => o.autofocus,
        #backdrop: (o) => o.backdrop,
        #barTransitionEnd: (o) => o.barTransitionEnd,
        #blurAction: (o) => o.blurAction,
        #cancelOrder: (o) => o.cancelOrder,
        #cancelQuota: (o) => o.cancelQuota,
        #cancelQuotaInvalid: (o) => o.cancelQuotaInvalid,
        #changeStorage: (o) => o.changeStorage,
        #checkboxAnimationEnd: (o) => o.checkboxAnimationEnd,
        #checked: (o) => o.checked,
        #closeSelector: (o) => o.closeSelector,
        #confirmOrder: (o) => o.confirmOrder,
        #container: (o) => o.container,
        #contextMenuAction: (o) => o.contextMenuAction,
        #creareOrder: (o) => o.creareOrder,
        #customer: (o) => o.customer,
        #customerGroup: (o) => o.customerGroup,
        #customerGroups: (o) => o.customerGroups,
        #customerHuphendId: (o) => o.customerHuphendId,
        #customerNameCn: (o) => o.customerNameCn,
        #customerNameEn: (o) => o.customerNameEn,
        #customers: (o) => o.customers,
        #deliverOrder: (o) => o.deliverOrder,
        #directorDispalyName: (o) => o.directorDispalyName,
        #directorLoginName: (o) => o.directorLoginName,
        #directorPassword: (o) => o.directorPassword,
        #disabled: (o) => o.disabled,
        #dismiss: (o) => o.dismiss,
        #displayName: (o) => o.displayName,
        #display_name: (o) => o.display_name,
        #downAction: (o) => o.downAction,
        #editOrder: (o) => o.editOrder,
        #editQuota: (o) => o.editQuota,
        #editQuotaInvalid: (o) => o.editQuotaInvalid,
        #editTemplate: (o) => o.editTemplate,
        #email: (o) => o.email,
        #emailInvalid: (o) => o.emailInvalid,
        #employees: (o) => o.employees,
        #enumerate: (o) => o.enumerate,
        #focusAction: (o) => o.focusAction,
        #heading: (o) => o.heading,
        #icon: (o) => o.icon,
        #iconSrc: (o) => o.iconSrc,
        #image_url: (o) => o.image_url,
        #index: (o) => o.index,
        #inputBlurAction: (o) => o.inputBlurAction,
        #inputChangeAction: (o) => o.inputChangeAction,
        #inputFocusAction: (o) => o.inputFocusAction,
        #inputValue: (o) => o.inputValue,
        #invalid: (o) => o.invalid,
        #keypressAction: (o) => o.keypressAction,
        #label: (o) => o.label,
        #launcherSelect: (o) => o.launcherSelect,
        #layered: (o) => o.layered,
        #loginBtnText: (o) => o.loginBtnText,
        #loginName: (o) => o.loginName,
        #logining: (o) => o.logining,
        #makeQuota: (o) => o.makeQuota,
        #makeQuotaInvalid: (o) => o.makeQuotaInvalid,
        #max: (o) => o.max,
        #maxlength: (o) => o.maxlength,
        #min: (o) => o.min,
        #multi: (o) => o.multi,
        #multiline: (o) => o.multiline,
        #name_cn: (o) => o.name_cn,
        #narrowMode: (o) => o.narrowMode,
        #newEmployeeClicked: (o) => o.newEmployeeClicked,
        #newProductClicked: (o) => o.newProductClicked,
        #nobar: (o) => o.nobar,
        #onDeleteClicked: (o) => o.onDeleteClicked,
        #onDeleteConfirm: (o) => o.onDeleteConfirm,
        #onListItemClicked: (o) => o.onListItemClicked,
        #onLoginClicked: (o) => o.onLoginClicked,
        #onSelectCustomer: (o) => o.onSelectCustomer,
        #onSelectCustomerGroup: (o) => o.onSelectCustomerGroup,
        #onSelectUser: (o) => o.onSelectUser,
        #onSubmitClicked: (o) => o.onSubmitClicked,
        #opened: (o) => o.opened,
        #orderAdvenceDay: (o) => o.orderAdvenceDay,
        #orderDayInvalid: (o) => o.orderDayInvalid,
        #panelSelected: (o) => o.panelSelected,
        #password: (o) => o.password,
        #pattern: (o) => o.pattern,
        #placeholder: (o) => o.placeholder,
        #product: (o) => o.product,
        #productId: (o) => o.productId,
        #productImageUrl: (o) => o.productImageUrl,
        #productIsDescimal: (o) => o.productIsDescimal,
        #productNameCn: (o) => o.productNameCn,
        #productNameEn: (o) => o.productNameEn,
        #productType: (o) => o.productType,
        #productUnitName: (o) => o.productUnitName,
        #products: (o) => o.products,
        #raisedButton: (o) => o.raisedButton,
        #readonly: (o) => o.readonly,
        #receiveOrder: (o) => o.receiveOrder,
        #required: (o) => o.required,
        #responsiveWidth: (o) => o.responsiveWidth,
        #rows: (o) => o.rows,
        #selected: (o) => o.selected,
        #selectedItem: (o) => o.selectedItem,
        #selectionSelect: (o) => o.selectionSelect,
        #src: (o) => o.src,
        #step: (o) => o.step,
        #submenu: (o) => o.submenu,
        #tabSelected: (o) => o.tabSelected,
        #text: (o) => o.text,
        #tokenList: (o) => o.tokenList,
        #transition: (o) => o.transition,
        #type: (o) => o.type,
        #upAction: (o) => o.upAction,
        #user: (o) => o.user,
        #users: (o) => o.users,
        #value: (o) => o.value,
        #valueattr: (o) => o.valueattr,
        #viewHistory: (o) => o.viewHistory,
        #viewStatistic: (o) => o.viewStatistic,
        #z: (o) => o.z,
      },
      setters: {
        #actioning: (o, v) { o.actioning = v; },
        #autoCloseDisabled: (o, v) { o.autoCloseDisabled = v; },
        #backdrop: (o, v) { o.backdrop = v; },
        #cancelOrder: (o, v) { o.cancelOrder = v; },
        #cancelQuota: (o, v) { o.cancelQuota = v; },
        #cancelQuotaInvalid: (o, v) { o.cancelQuotaInvalid = v; },
        #changeStorage: (o, v) { o.changeStorage = v; },
        #closeSelector: (o, v) { o.closeSelector = v; },
        #confirmOrder: (o, v) { o.confirmOrder = v; },
        #container: (o, v) { o.container = v; },
        #creareOrder: (o, v) { o.creareOrder = v; },
        #customerHuphendId: (o, v) { o.customerHuphendId = v; },
        #customerNameCn: (o, v) { o.customerNameCn = v; },
        #customerNameEn: (o, v) { o.customerNameEn = v; },
        #deliverOrder: (o, v) { o.deliverOrder = v; },
        #directorDispalyName: (o, v) { o.directorDispalyName = v; },
        #directorLoginName: (o, v) { o.directorLoginName = v; },
        #directorPassword: (o, v) { o.directorPassword = v; },
        #displayName: (o, v) { o.displayName = v; },
        #editOrder: (o, v) { o.editOrder = v; },
        #editQuota: (o, v) { o.editQuota = v; },
        #editQuotaInvalid: (o, v) { o.editQuotaInvalid = v; },
        #editTemplate: (o, v) { o.editTemplate = v; },
        #email: (o, v) { o.email = v; },
        #emailInvalid: (o, v) { o.emailInvalid = v; },
        #icon: (o, v) { o.icon = v; },
        #iconSrc: (o, v) { o.iconSrc = v; },
        #index: (o, v) { o.index = v; },
        #inputValue: (o, v) { o.inputValue = v; },
        #label: (o, v) { o.label = v; },
        #launcherSelect: (o, v) { o.launcherSelect = v; },
        #layered: (o, v) { o.layered = v; },
        #loginBtnText: (o, v) { o.loginBtnText = v; },
        #loginName: (o, v) { o.loginName = v; },
        #logining: (o, v) { o.logining = v; },
        #makeQuota: (o, v) { o.makeQuota = v; },
        #makeQuotaInvalid: (o, v) { o.makeQuotaInvalid = v; },
        #multi: (o, v) { o.multi = v; },
        #name_cn: (o, v) { o.name_cn = v; },
        #narrowMode: (o, v) { o.narrowMode = v; },
        #opened: (o, v) { o.opened = v; },
        #orderAdvenceDay: (o, v) { o.orderAdvenceDay = v; },
        #orderDayInvalid: (o, v) { o.orderDayInvalid = v; },
        #panelSelected: (o, v) { o.panelSelected = v; },
        #password: (o, v) { o.password = v; },
        #productId: (o, v) { o.productId = v; },
        #productImageUrl: (o, v) { o.productImageUrl = v; },
        #productIsDescimal: (o, v) { o.productIsDescimal = v; },
        #productNameCn: (o, v) { o.productNameCn = v; },
        #productNameEn: (o, v) { o.productNameEn = v; },
        #productType: (o, v) { o.productType = v; },
        #productUnitName: (o, v) { o.productUnitName = v; },
        #receiveOrder: (o, v) { o.receiveOrder = v; },
        #selected: (o, v) { o.selected = v; },
        #selectedItem: (o, v) { o.selectedItem = v; },
        #src: (o, v) { o.src = v; },
        #submenu: (o, v) { o.submenu = v; },
        #tabSelected: (o, v) { o.tabSelected = v; },
        #transition: (o, v) { o.transition = v; },
        #valueattr: (o, v) { o.valueattr = v; },
        #viewHistory: (o, v) { o.viewHistory = v; },
        #viewStatistic: (o, v) { o.viewStatistic = v; },
        #z: (o, v) { o.z = v; },
      },
      parents: {
        smoke_5.CustomerSubpage: _M0,
        smoke_4.EmployeeSubpage: _M0,
        smoke_6.MainPage: _M0,
        smoke_3.ProductSubpage: _M0,
        smoke_0.SystemSubPage: _M0,
        _M0: smoke_1.PolymerElement,
      },
      declarations: {
        smoke_5.CustomerSubpage: {
          #actioning: const Declaration(#actioning, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #cancelOrder: const Declaration(#cancelOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #changeStorage: const Declaration(#changeStorage, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #creareOrder: const Declaration(#creareOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #customerHuphendId: const Declaration(#customerHuphendId, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #customerNameCn: const Declaration(#customerNameCn, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #customerNameEn: const Declaration(#customerNameEn, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #directorDispalyName: const Declaration(#directorDispalyName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #directorLoginName: const Declaration(#directorLoginName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #directorPassword: const Declaration(#directorPassword, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #displayName: const Declaration(#displayName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #editOrder: const Declaration(#editOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #editTemplate: const Declaration(#editTemplate, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #loginName: const Declaration(#loginName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #panelSelected: const Declaration(#panelSelected, int, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #password: const Declaration(#password, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #receiveOrder: const Declaration(#receiveOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
        },
        smoke_4.EmployeeSubpage: {
          #actioning: const Declaration(#actioning, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #cancelOrder: const Declaration(#cancelOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #changeStorage: const Declaration(#changeStorage, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #confirmOrder: const Declaration(#confirmOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #creareOrder: const Declaration(#creareOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #deliverOrder: const Declaration(#deliverOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #displayName: const Declaration(#displayName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #editOrder: const Declaration(#editOrder, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #loginName: const Declaration(#loginName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #password: const Declaration(#password, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #viewHistory: const Declaration(#viewHistory, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #viewStatistic: const Declaration(#viewStatistic, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
        },
        smoke_6.MainPage: {
          #launcherSelect: const Declaration(#launcherSelect, int, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #loginBtnText: const Declaration(#loginBtnText, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #loginName: const Declaration(#loginName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #logining: const Declaration(#logining, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #password: const Declaration(#password, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #tabSelected: const Declaration(#tabSelected, int, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_1.published]),
        },
        smoke_3.ProductSubpage: {
          #actioning: const Declaration(#actioning, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productId: const Declaration(#productId, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productImageUrl: const Declaration(#productImageUrl, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productIsDescimal: const Declaration(#productIsDescimal, int, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productNameCn: const Declaration(#productNameCn, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productNameEn: const Declaration(#productNameEn, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productType: const Declaration(#productType, int, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #productUnitName: const Declaration(#productUnitName, String, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
        },
        smoke_0.SystemSubPage: {
          #actioning: const Declaration(#actioning, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #cancelQuota: const Declaration(#cancelQuota, dynamic, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #cancelQuotaInvalid: const Declaration(#cancelQuotaInvalid, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #editQuota: const Declaration(#editQuota, dynamic, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #editQuotaInvalid: const Declaration(#editQuotaInvalid, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #email: const Declaration(#email, dynamic, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #emailInvalid: const Declaration(#emailInvalid, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #makeQuota: const Declaration(#makeQuota, dynamic, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #makeQuotaInvalid: const Declaration(#makeQuotaInvalid, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #orderAdvenceDay: const Declaration(#orderAdvenceDay, dynamic, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
          #orderDayInvalid: const Declaration(#orderDayInvalid, bool, kind: PROPERTY, annotations: const [smoke_2.reflectable, smoke_2.observable]),
        },
      },
      names: {
        #$: r'$',
        #actioning: r'actioning',
        #activate: r'activate',
        #active: r'active',
        #autoCloseDisabled: r'autoCloseDisabled',
        #autofocus: r'autofocus',
        #backdrop: r'backdrop',
        #barTransitionEnd: r'barTransitionEnd',
        #blurAction: r'blurAction',
        #cancelOrder: r'cancelOrder',
        #cancelQuota: r'cancelQuota',
        #cancelQuotaInvalid: r'cancelQuotaInvalid',
        #changeStorage: r'changeStorage',
        #checkboxAnimationEnd: r'checkboxAnimationEnd',
        #checked: r'checked',
        #closeSelector: r'closeSelector',
        #confirmOrder: r'confirmOrder',
        #container: r'container',
        #contextMenuAction: r'contextMenuAction',
        #creareOrder: r'creareOrder',
        #customer: r'customer',
        #customerGroup: r'customerGroup',
        #customerGroups: r'customerGroups',
        #customerHuphendId: r'customerHuphendId',
        #customerNameCn: r'customerNameCn',
        #customerNameEn: r'customerNameEn',
        #customers: r'customers',
        #deliverOrder: r'deliverOrder',
        #directorDispalyName: r'directorDispalyName',
        #directorLoginName: r'directorLoginName',
        #directorPassword: r'directorPassword',
        #disabled: r'disabled',
        #dismiss: r'dismiss',
        #displayName: r'displayName',
        #display_name: r'display_name',
        #downAction: r'downAction',
        #editOrder: r'editOrder',
        #editQuota: r'editQuota',
        #editQuotaInvalid: r'editQuotaInvalid',
        #editTemplate: r'editTemplate',
        #email: r'email',
        #emailInvalid: r'emailInvalid',
        #employees: r'employees',
        #enumerate: r'enumerate',
        #focusAction: r'focusAction',
        #heading: r'heading',
        #icon: r'icon',
        #iconSrc: r'iconSrc',
        #image_url: r'image_url',
        #index: r'index',
        #inputBlurAction: r'inputBlurAction',
        #inputChangeAction: r'inputChangeAction',
        #inputFocusAction: r'inputFocusAction',
        #inputValue: r'inputValue',
        #invalid: r'invalid',
        #keypressAction: r'keypressAction',
        #label: r'label',
        #launcherSelect: r'launcherSelect',
        #layered: r'layered',
        #loginBtnText: r'loginBtnText',
        #loginName: r'loginName',
        #logining: r'logining',
        #makeQuota: r'makeQuota',
        #makeQuotaInvalid: r'makeQuotaInvalid',
        #max: r'max',
        #maxlength: r'maxlength',
        #min: r'min',
        #multi: r'multi',
        #multiline: r'multiline',
        #name_cn: r'name_cn',
        #narrowMode: r'narrowMode',
        #newEmployeeClicked: r'newEmployeeClicked',
        #newProductClicked: r'newProductClicked',
        #nobar: r'nobar',
        #onDeleteClicked: r'onDeleteClicked',
        #onDeleteConfirm: r'onDeleteConfirm',
        #onListItemClicked: r'onListItemClicked',
        #onLoginClicked: r'onLoginClicked',
        #onSelectCustomer: r'onSelectCustomer',
        #onSelectCustomerGroup: r'onSelectCustomerGroup',
        #onSelectUser: r'onSelectUser',
        #onSubmitClicked: r'onSubmitClicked',
        #opened: r'opened',
        #orderAdvenceDay: r'orderAdvenceDay',
        #orderDayInvalid: r'orderDayInvalid',
        #panelSelected: r'panelSelected',
        #password: r'password',
        #pattern: r'pattern',
        #placeholder: r'placeholder',
        #product: r'product',
        #productId: r'productId',
        #productImageUrl: r'productImageUrl',
        #productIsDescimal: r'productIsDescimal',
        #productNameCn: r'productNameCn',
        #productNameEn: r'productNameEn',
        #productType: r'productType',
        #productUnitName: r'productUnitName',
        #products: r'products',
        #raisedButton: r'raisedButton',
        #readonly: r'readonly',
        #receiveOrder: r'receiveOrder',
        #required: r'required',
        #responsiveWidth: r'responsiveWidth',
        #rows: r'rows',
        #selected: r'selected',
        #selectedItem: r'selectedItem',
        #selectionSelect: r'selectionSelect',
        #src: r'src',
        #step: r'step',
        #submenu: r'submenu',
        #tabSelected: r'tabSelected',
        #text: r'text',
        #tokenList: r'tokenList',
        #transition: r'transition',
        #type: r'type',
        #upAction: r'upAction',
        #user: r'user',
        #users: r'users',
        #value: r'value',
        #valueattr: r'valueattr',
        #viewHistory: r'viewHistory',
        #viewStatistic: r'viewStatistic',
        #z: r'z',
      }));
  new LogInjector().injectLogsFromUrl('mobileordering.html._buildLogs');
  configureForDeployment([
      i0.upgradeCoreToolbar,
      i1.upgradeCoreSelection,
      i2.upgradeCoreSelector,
      i3.upgradePaperRipple,
      i4.upgradePaperTab,
      i5.upgradePaperTabs,
      i6.upgradeCoreMeta,
      i7.upgradeCoreTransition,
      i8.upgradeCoreKeyHelper,
      i9.upgradeCoreOverlayLayer,
      i10.upgradeCoreOverlay,
      i11.upgradeCoreTransitionCss,
      i12.upgradeCoreMediaQuery,
      i13.upgradePaperToast,
      i14.upgradeCoreIconset,
      i15.upgradeCoreIcon,
      i16.upgradePaperFocusable,
      i17.upgradePaperShadow,
      i18.upgradePaperButton,
      i19.upgradeCoreAnimatedPages,
      i20.upgradeCorePages,
      i21.upgradeCoreIconsetSvg,
      i22.upgradeCoreField,
      i23.upgradeCoreInput,
      () => Polymer.register('system-subpage', i24.SystemSubPage),
      i25.upgradePaperIconButton,
      i26.upgradePaperFab,
      i27.upgradePaperRadioButton,
      i28.upgradePaperRadioGroup,
      i29.upgradePaperDialog,
      () => Polymer.register('product-subpage', i30.ProductSubpage),
      i31.upgradePaperCheckbox,
      i32.upgradePaperDialogTransition,
      () => Polymer.register('employee-subpage', i33.EmployeeSubpage),
      i34.upgradeCoreMenu,
      i35.upgradeCoreItem,
      i36.upgradeCoreCollapse,
      i37.upgradeCoreSubmenu,
      () => Polymer.register('customer-subpage', i38.CustomerSubpage),
      () => Polymer.register('main-page', i39.MainPage),
    ]);
  i40.main();
}
