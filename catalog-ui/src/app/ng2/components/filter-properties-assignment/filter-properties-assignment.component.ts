/**
 * Created by rc2122 on 5/16/2017.
 */
import {Component, Input, Output, EventEmitter, ViewChild} from '@angular/core';
import {ButtonModel, ButtonsModelMap, FilterPropertiesAssignmentData} from "app/models";
import {PopoverComponent} from "../popover/popover.component";
import * as sdcConfig from "../../../../../configurations/dev"

@Component({
    selector: 'filter-properties-assignment',
    templateUrl: './filter-properties-assignment.component.html',
    styleUrls: ['./filter-properties-assignment.component.less']
})

export class FilterPropertiesAssignmentComponent {
    @Input() componentType: string;
    @Output() searchProperties: EventEmitter<FilterPropertiesAssignmentData> = new EventEmitter<FilterPropertiesAssignmentData>();
    footerButtons:ButtonsModelMap = {};
    typesOptions:Array<string>;//All optional types
    selectedTypes:Object = {};
    allSelected:boolean = false;//if all option selected
    filterData:FilterPropertiesAssignmentData = new FilterPropertiesAssignmentData();
    @ViewChild('filterPopover') filterPopover: PopoverComponent;

    ngOnInit() {
        this.footerButtons['Apply'] = new ButtonModel('Apply', 'blue', this.search, this.someTypesSelectedAndThereIsPropertyName);
        this.footerButtons['Close'] = new ButtonModel('Close', 'grey', this.close);
        this.componentType = this.componentType.toLocaleLowerCase();
        this.typesOptions = sdcConfig.resourceTypesFilter[this.componentType];
    }

    selectAll = () => {
        _.forEach(this.typesOptions, (type) => {
            this.selectedTypes[type] = this.allSelected;
        });
    }

    onTypeSelected = (type:string) => {
        if(!this.selectedTypes[type]){
            this.allSelected = false;//unselected 'All'
        }
    };

    search = () => {
        console.log('search props');
        this.filterData.selectedTypes = [];
        _.forEach(sdcConfig.resourceTypesFilter[this.componentType], (type) => {
            if(this.selectedTypes[type]){
                this.filterData.selectedTypes.push(type);
            }
        });
        this.searchProperties.emit(this.filterData);
        this.filterPopover.hide();
    }

    close = () => {
        this.filterPopover.hide();
    }

    someTypesSelectedAndThereIsPropertyName = ():boolean => {
        if( _.find(Object.keys(this.selectedTypes),(key) => {
            return this.selectedTypes[key];
        }) && this.filterData.propertyName ){
            return null
        }
        return true;
    }

    clearAll = ():void => {
        this.filterData.propertyName = "";
        _.forEach(this.selectedTypes,(value, key) => {
            this.selectedTypes[key] = false;
        });
        this.allSelected = false;
    }

}