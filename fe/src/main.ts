import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import PrimeVue from 'primevue/config';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import Toolbar from 'primevue/toolbar';
import Dialog from 'primevue/dialog';
import Textarea from 'primevue/textarea';
import Calendar from 'primevue/calendar';
import Menubar from 'primevue/menubar';
import Tooltip from 'primevue/tooltip';
import ToastService from 'primevue/toastservice';


import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

const app = createApp(App);

app.use(router);

app.use(PrimeVue);
app.use(ToastService);

app.component('DataTable', DataTable);
app.component('Column', Column);
app.component('InputText', InputText);
app.component('InputNumber', InputNumber);
app.component('Button', Button);
app.component('Toast', Toast);
app.component('Toolbar', Toolbar);
app.component('Dialog', Dialog);
app.component('Textarea', Textarea);
app.component('Calendar', Calendar);
app.component('Menubar', Menubar);
app.directive('tooltip', Tooltip);

app.mount("#app");
