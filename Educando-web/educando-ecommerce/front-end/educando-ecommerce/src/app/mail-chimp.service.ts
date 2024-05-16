import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MailChimpService {
  private apiKey = environment.mailchimpApiKey;
  
  constructor(private http: HttpClient) { }
  
  addEmail(body: any) {
    const url = 'https://us17.api.mailchimp.com/3.0/lists/43faf39f64/members/';
    
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Authorization', 'Bearer ' + this.apiKey);
    headers = headers.append('Access-Control-Allow-Origin', '*');
    console.log(headers)
    debugger
    return this.http.post(url, body, { headers: headers });
    console.log(this.apiKey);
  }
}
