import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EntryComponent } from './entry.component';
import { MatExpansionModule } from '@angular/material/expansion';

@NgModule({
  declarations: [EntryComponent],
  imports: [CommonModule, MatExpansionModule],
})
export class EntryModule {}
