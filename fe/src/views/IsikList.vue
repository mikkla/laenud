<template>
	<div>
        <DataTable :value="isikList" :paginator="true" :rows="10" v-model:filters="filters"
            :totalRecords="totalRecords" responsiveLayout="scroll"
            :globalFilterFields="['nimi','aadress','epost']">
            <template #header>
                <div class="table-header flex flex-column md:flex-row md:justiify-content-between">
                    <h1 class="mb-2 md:m-0 p-as-md-center">Isikud</h1>
                    <Toolbar>
                        <template #start>
                            <Button label="Lisa isik" icon="pi pi-plus" class="p-button-success mr-2" @click="openNew" />
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
            <Column field="nimi" header="Nimi" :sortable="true">
            </Column>
            <Column field="epost" header="E-post" :sortable="true">
            </Column>
            <Column header="Sünniaeg" dataType="date">
                <template #body="{data}">
                    {{formatDate(data.synniaeg)}}
                </template>
            </Column>
            <Column field="aadress" header="Aadress" :sortable="true">
            </Column>
            <Column :exportable="false">
                <template #body="slotProps">
                    <Button icon="pi pi-pencil" class="p-button-rounded p-button-primary p-button-text"
                        @click="editIsik(slotProps.data)" v-tooltip.bottom="'Muuda'" />
                    <Button icon="pi pi-dollar" class="p-button-rounded p-button-success p-button-text"
                        @click="$router.push(`/isik/${slotProps.data.id}`)" v-tooltip.bottom="'Taotlused'" />
                    <Button icon="pi pi-trash" class="p-button-rounded p-button-danger p-button-text"
                        @click="confirmDeleteIsik(slotProps.data)" v-tooltip.bottom="'Kustuta'" />
                </template>
            </Column>
        </DataTable>

        <Dialog v-model:visible="dialog" :style="{width: '450px'}" header="Isiku detailid" :modal="true" class="p-fluid">
            <div class="field">
                <label for="nimi">Nimi</label>
                <InputText id="nimi" v-model.trim="isik.nimi" required="true" autofocus :class="{'p-invalid': submitted && !isik.nimi}" />
                <small class="p-error" v-if="submitted && !isik.nimi">Nimi on kohustuslik</small>
            </div>
            <div class="field">
                <label for="epost">E-post</label>
                <InputText id="epost" v-model.trim="isik.epost" required="true" :class="{'p-invalid': submitted && !isik.epost}" />
                <small class="p-error" v-if="submitted && !isik.epost">E-post on kohustuslik</small>
            </div>

            <div class="field">
                <label for="synniaeg">Sünniaeg</label>
                <Calendar v-model="isik.synniaeg" dateFormat="dd.mm.yy" required="true" :class="{'p-invalid': submitted && !isik.synniaeg}" />
                <small class="p-error" v-if="submitted && !isik.synniaeg">Sünniaeg on kohustuslik</small>
            </div>

            <div class="field">
                <label for="aadress">Aadress</label>
                <Textarea id="aadress" v-model="isik.aadress" required="true" rows="3" cols="20" :class="{'p-invalid': submitted && !isik.aadress}" />
                <small class="p-error" v-if="submitted && !isik.aadress">Aadress on kohustuslik</small>
            </div>
            <template #footer>
                <Button label="Katkesta" icon="pi pi-times" class="p-button-text" @click="hideDialog"/>
                <Button label="Salvesta" icon="pi pi-check" class="p-button-text" @click="saveIsik" />
            </template>
        </Dialog>

        <Dialog v-model:visible="deleteDialog" :style="{width: '450px'}" header="Kinnita" :modal="true">
            <span v-if="isik"> Kustutan <b>{{isik.nimi}}</b>?</span>
            <template #footer>
                <Button label="Ei" icon="pi pi-times" class="p-button-text" @click="deleteDialog = false"/>
                <Button label="Jah" icon="pi pi-check" class="p-button-text" @click="deleteIsik" />
            </template>
        </Dialog>
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import IsikDataService from '@/services/IsikDataService';
import type Isik from "@/types/Isik";
import type ResponseData from "@/types/ResponseData";
import {FilterMatchMode} from 'primevue/api';
import type { DataTableFilterMeta } from 'primevue/datatable';

export default defineComponent({
    data() {
        return {
            loading: false,
            totalRecords: 0,
            isikList: [] as Isik[],
            filterText: '',
            filters: {
                'global': {value: '', matchMode: FilterMatchMode.CONTAINS},
            } as DataTableFilterMeta,
            isik: {} as Isik,
            submitted: false,
            dialog: false,
            deleteDialog: false,

        }
    },
    mounted() {
        this.loading = true;
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

            IsikDataService.getAll()
                    .then((response: ResponseData) => {
                        this.isikList = response.data;
                        this.totalRecords = response.data.length;
                        this.loading = false;
                }
            );

        },
        formatDate(value: string) {
            return new Date(value).toLocaleDateString('et-EE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            });
        },
        openNew() {
            this.clearIsik(this.isik);
            this.submitted = false;
            this.dialog = true;
        },
        clearIsik(isik: Isik) {
            isik.id = 0;
            isik.nimi = '';
            isik.aadress = '';
            isik.epost = '';
            isik.synniaeg = undefined;
        } ,
        hideDialog() {
            this.dialog = false;
            this.submitted = false;
        },
        saveIsik() {
            this.submitted = true;

			if (this.isik.nimi.trim() && this.isik.epost.trim() && this.isik.synniaeg && this.isik.aadress.trim()) {
                this.dialog = false;
                if (this.isik.id) {
                    IsikDataService.update(this.isik.id, this.isik)
                        .then((response: ResponseData) => {
                            this.$toast.add({severity:'success', summary: 'OK', detail: 'Isik salvestatud', life: 3000});
                            this.loadData();
                            this.clearIsik(this.isik);
                        }).catch(error => {
                            this.$toast.add({severity:'error', summary: 'Viga', detail: 'Salvestamine ebaõnnestus', life: 3000});
                        })
                }
                else {
                    IsikDataService.create(this.isik)
                        .then((response: ResponseData) => {
                            this.$toast.add({severity:'success', summary: 'OK', detail: 'Iisk lisatud', life: 3000});
                            this.loadData();
                            this.clearIsik(this.isik);
                        }).catch(error => {
                            this.$toast.add({severity:'error', summary: 'Viga', detail: 'Lisamine ebaõnnestus', life: 3000});
                        })
                }
            }
        },
        editIsik(isik: Isik) {
            this.isik = {...isik};
            this.dialog = true;
        },
        confirmDeleteIsik(isik: Isik) {
            this.isik = isik;
            this.deleteDialog = true;
        },
        deleteIsik() {
            this.deleteDialog = false;
            IsikDataService.delete(this.isik.id)
                .then((response: ResponseData) => {
                    this.$toast.add({severity:'success', summary: 'OK', detail: 'Isik kustutatud', life: 3000});
                    this.loadData();
                    this.clearIsik(this.isik);
                }).catch(error => {
                    console.log(error);
                    this.$toast.add({severity:'error', summary: 'Viga', detail: 'Kustutamine ebaõnnestus', life: 3000});
                })
        }
    }
})
</script>