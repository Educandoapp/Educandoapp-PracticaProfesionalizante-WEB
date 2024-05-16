import { Component } from '@angular/core';
import { MailChimpService } from 'src/app/mail-chimp.service';
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  title = 'educando-ecommerce';
  Email:string=''

  constructor(private mailChimpService:MailChimpService){

  }
  saveEmail() {
    let bodyMailChimp = { email_address: this.Email, status: 'subscribed' };
    this.mailChimpService.addEmail(bodyMailChimp).subscribe();
  }
}
