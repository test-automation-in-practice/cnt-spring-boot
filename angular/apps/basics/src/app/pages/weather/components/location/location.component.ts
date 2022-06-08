import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'basics-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss'],
})
export class LocationComponent {
  static locationErrors = [
    { errorStatus: 'required', errorMessage: 'Please enter a location before searching!' },
    { errorStatus: 'minlength', errorMessage: 'At least 3 characters are needed!' },
    { errorStatus: 'pattern', errorMessage: 'Numbers are not allowed!' },
  ];
  @Input() defaultLocation: string | undefined = undefined;
  @Output() searched = new EventEmitter<string>();
  form: FormGroup = new FormGroup({
    location: new FormControl('', [Validators.required, Validators.pattern(new RegExp('^[a-zA-Z -]*$')), Validators.minLength(3)]),
  });

  startSearch() {
    if (this.form?.valid) {
      this.searched.emit(this.form.controls['location'].value);
    }
  }
}
