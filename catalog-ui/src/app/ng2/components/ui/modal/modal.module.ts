import { NgModule } from "@angular/core";
import { CommonModule } from '@angular/common';
import { AngularDraggableModule } from 'angular2-draggable';
import { ModalService } from 'app/ng2/services/modal.service';
import { ErrorMessageComponent } from "./error-message/error-message.component";
import {ModalComponent} from "./modal.component";

@NgModule({
    declarations: [
        ModalComponent,
        ErrorMessageComponent
    ],
    imports: [CommonModule, AngularDraggableModule],
    exports: [ModalComponent, ErrorMessageComponent],
    entryComponents: [ //need to add anything that will be dynamically created
        ModalComponent,
        ErrorMessageComponent
    ],
    providers: [ModalService]
})
export class ModalModule {

}