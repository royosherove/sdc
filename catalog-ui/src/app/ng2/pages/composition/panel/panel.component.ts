/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

import * as _ from "lodash";
import { Component, Inject, Input, Output, EventEmitter, AfterViewInit, SimpleChanges, HostBinding } from "@angular/core";
import { Component as TopologyTemplate, ComponentInstance, IAppMenu } from "app/models";
import { PolicyInstance } from 'app/models/graph/zones/policy-instance';
import { TranslateService } from 'app/ng2/shared/translator/translate.service';
import { ZoneInstanceType } from "app/models/graph/zones/zone-instance";
import { GroupsService } from "../../../services/groups.service";
import { PoliciesService } from "../../../services/policies.service";
import { SdcUiComponents } from "sdc-ui/lib/angular";
import { IZoneService } from "../../../../models/graph/zones/zone";

@Component({
    selector: 'ng2-composition-panel',
    templateUrl: './panel.component.html',
    styleUrls: ['./panel.component.less'],
    providers: [TranslateService]
})
export class CompositionPanelComponent {

    @Input() topologyTemplate: TopologyTemplate;
    @Input() selectedZoneInstanceType: ZoneInstanceType;
    @Input() selectedZoneInstanceId: string;
    @Input() selectedZoneInstanceName: string;
    @Input() nonCertified: boolean;
    @Input() isViewOnly: boolean;
    @Input() isLoading: boolean;


    @HostBinding('class') classes = 'component-details-panel';

    private zoneInstanceType = ZoneInstanceType; // Expose ZoneInstanceType to use in template.

    constructor(){
    }

    private setIsLoading = (value):void => {
        this.isLoading = value;
    }

}
