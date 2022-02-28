<template>
	<div>
        <DataTable :value="taotlusList" :paginator="true" :rows="10" v-model:filters="filters"
            :totalRecords="totalRecords" responsiveLayout="scroll"
            :globalFilterFields="['summa']">
            <template #header>
                <div class="table-header flex flex-column md:flex-row md:justiify-content-between">
                    <h1 class="mb-2 md:m-0 p-as-md-center">Taotlused{{lisaTitle}}</h1>
                    <Toolbar>
                        <template #start>
                            <Button label="Lisa taotlus" icon="pi pi-plus" class="p-button-success mr-2" @click="openNew" v-if="isikId" />
                        </template>
                        <template #end>
                            <span class="p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filterText" placeholder="Otsi..." />
                            </span>
                        </template>
                    </Toolbar>
                </div>
            </template>
            <Column field="summa" header="Summa" :sortable="true">
            </Column>
            <Column header="Tähtaeg" dataType="date">
                <template #body="{data}">
                    {{formatDate(data.tahtaeg)}}
                </template>
            </Column>
            <Column :exportable="false">
                <template #body="slotProps">
                    <Button icon="pi pi-pencil" class="p-button-rounded p-button-primary p-button-text"
                        @click="editTaotlus(slotProps.data)" v-tooltip.bottom="'Muuda'" />
                    <Button icon="pi pi-trash" class="p-button-rounded p-button-danger p-button-text"
                        @click="confirmDeleteTaotlus(slotProps.data)" v-tooltip.bottom="'Kustuta'" />
                </template>
            </Column>
        </DataTable>

        <Dialog v-model:visible="dialog" :style="{width: '450px'}" header="Isiku detailid" :modal="true" class="p-fluid">
            <div class="field">
                <label for="nimi">Summa</label>
                <InputNumber id="price" v-model="taotlus.summa" mode="currency" :min="0" showButtons currency="EUR" locale="et-EE"
                    required="true" autofocus />
                <small class="p-error" v-if="submitted && !taotlus.summa">Summa on kohustuslik</small>
            </div>

            <div class="field">
                <label for="synniaeg">Tähtaeg</label>
                <Calendar v-model="taotlus.tahtaeg" dateFormat="dd.mm.yy" required="true" :class="{'p-invalid': submitted && !taotlus.tahtaeg}" />
                <small class="p-error" v-if="submitted && !taotlus.tahtaeg">Tähtaeg on kohustuslik</small>
            </div>

            <template #footer>
                <Button label="Katkesta" icon="pi pi-times" class="p-button-text" @click="hideDialog"/>
                <Button label="Salvesta" icon="pi pi-check" class="p-button-text" @click="saveTaotlus" />
            </template>
        </Dialog>

        <Dialog v-model:visible="deleteDialog" :style="{width: '450px'}" header="Kinnita" :modal="true">
            <span v-if="taotlus"> Kustutan <b>{{taotlus.summa}}</b>?</span>
            <template #footer>
                <Button label="Ei" icon="pi pi-times" class="p-button-text" @click="deleteDialog = false"/>
                <Button label="Jah" icon="pi pi-check" class="p-button-text" @click="deleteTaotlus" />
            </template>
        </Dialog>
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import IsikDataService from '@/services/IsikDataService';
import TaotlusDataService from '@/services/TaotlusDataService';
import type Taotlus from "@/types/Taotlus";
import type ResponseData from "@/types/ResponseData";
import {FilterMatchMode} from 'primevue/api';
import type { DataTableFilterMeta } from 'primevue/datatable';

export default defineComponent({
    data() {
        return {
            loading: false,
            totalRecords: 0,
            taotlusList: [] as Taotlus[],
            filterText: '',
            filters: {
                'global': {value: '', matchMode: FilterMatchMode.CONTAINS},
            } as DataTableFilterMeta,
            taotlus: {} as Taotlus,
            submitted: false,
            dialog: false,
            deleteDialog: false,
            lisaTitle: '',
            isikId: 0,
        }
    },
    mounted() {
        this.loading = true;
        this.isikId = this.$route.params.id ? +this.$route.params.id : 0;
        if (this.isikId) {
            IsikDataService.get(+this.$route.params.id)
                    .then((response: ResponseData) => {
                        this.lisaTitle = ': ' + response.data.nimi;
                }
            );
        }
        this.loadData();
    },
    watch: {
        filterText(value, oldValue) {
            const asd = this.filters['global'] as any;
            (asd.value as string) = this.filterText;
        }
    },
    methods: {
        loadData() {
            this.loading = true;
            if (this.isikId) {
                TaotlusDataService.getForIsik(this.isikId)
                    .then((response: ResponseData) => {
                        this.taotlusList = response.data;
                        this.totalRecords = response.data.length;
                        this.loading = false;
                    }
                );
            } else {
                TaotlusDataService.getAll()
                    .then((response: ResponseData) => {
                        this.taotlusList = response.data;
                        this.totalRecords = response.data.length;
                        this.loading = false;
                    }
                );
            }
        },
        formatDate(value: string) {
            return new Date(value).toLocaleDateString('et-EE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            });
        },
        openNew() {
            this.clearTaotlus(this.taotlus);
            this.submitted = false;
            this.dialog = true;
        },
        clearTaotlus(taotlus: Taotlus) {
            taotlus.id = 0;
            taotlus.summa = 0;
            taotlus.tahtaeg = undefined;
        } ,
        hideDialog() {
            this.dialog = false;
            this.submitted = false;
        },
        saveTaotlus() {
            this.submitted = true;

			if (this.taotlus.summa && this.taotlus.tahtaeg) {
                this.dialog = false;
                if (this.taotlus.id) {
                    TaotlusDataService.update(this.taotlus.id, this.taotlus)
                        .then((response: ResponseData) => {
                            this.$toast.add({severity:'success', summary: 'OK', detail: 'Taotlus salvestatud', life: 3000});
                            this.loadData();
                            this.clearTaotlus(this.taotlus);
                        }).catch(error => {
                            if (error.message && (error.message as string).includes('400')) {
                                this.$toast.add({severity:'error', summary: 'Viga', detail: 'Isik peab olema vähemalt 18 aastane', life: 3000});
                            } else {
                            this.$toast.add({severity:'error', summary: 'Viga', detail: 'Salvestamine ebaõnnestus', life: 3000});
                            }
                        })
                }
                else {
                    TaotlusDataService.create(this.isikId, this.taotlus)
                        .then((response: ResponseData) => {
                            this.$toast.add({severity:'success', summary: 'OK', detail: 'Taotlus lisatud', life: 3000});
                            this.loadData();
                            this.clearTaotlus(this.taotlus);
                        }).catch(error => {
                            if (error.message && (error.message as string).includes('400')) {
                                this.$toast.add({severity:'error', summary: 'Viga', detail: 'Isik peab olema vähemalt 18 aastane', life: 3000});
                            } else {
                            this.$toast.add({severity:'error', summary: 'Viga', detail: 'Salvestamine ebaõnnestus', life: 3000});
                            }
                        })
                }
            }
        },
        editTaotlus(taotlus: Taotlus) {
            this.taotlus = {...taotlus};
            this.dialog = true;
        },
        confirmDeleteTaotlus(taotlus: Taotlus) {
            this.taotlus = taotlus;
            this.deleteDialog = true;
        },
        deleteTaotlus() {
            this.deleteDialog = false;
            TaotlusDataService.delete(this.taotlus.id)
                .then((response: ResponseData) => {
                    this.$toast.add({severity:'success', summary: 'OK', detail: 'Taotlus kustutatud', life: 3000});
                    this.loadData();
                    this.clearTaotlus(this.taotlus);
                }).catch(error => {
                    console.log(error);
                    this.$toast.add({severity:'error', summary: 'Viga', detail: 'Kustutamine ebaõnnestus', life: 3000});
                })
        }
    }
})
</script>