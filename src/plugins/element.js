import Vue from 'vue'
import { 
    Button, 
    Form, 
    FormItem, 
    Input, 
    Container, 
    Header, 
    Aside, 
    Main, 
    Menu, 
    Submenu, 
    Message, 
    MenuItem,
    Breadcrumb,
    BreadcrumbItem,
    Card,
    Row,
    Col,
    Table,
    TableColumn,
    Switch,
    Tooltip,
    Pagination,
    Dialog,
    MessageBox,
    Select,
    Option,
    DatePicker,
    CheckboxGroup,
    Checkbox,
    Upload,
    RadioGroup,
    RadioButton    
} from 'element-ui'

Vue.use(Button)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Input)
Vue.use(Container)
Vue.use(Header)
Vue.use(Aside)
Vue.use(Main)
Vue.use(Menu)
Vue.use(Submenu)
Vue.prototype.$message = Message
Vue.use(MenuItem)
Vue.use(Breadcrumb)
Vue.use(BreadcrumbItem)
Vue.use(Card)
Vue.use(Row)
Vue.use(Col)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Switch)
Vue.use(Tooltip)
Vue.use(Pagination)
Vue.use(Dialog)
Vue.use(Select)
Vue.use(Option)
Vue.use(DatePicker)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Upload)
Vue.use(RadioGroup)
Vue.use(RadioButton)
Vue.prototype.$confirm = MessageBox.confirm
