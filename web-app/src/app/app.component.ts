import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'web-app';

  lastOrderId: string = "";
  products: any[] = [];
  defaultProducts: any[] = [
    {name:"chair", price: 15, quantity: 2},
    {name:"chair1", price: 15, quantity: 2},
    {name:"chair2", price: 15, quantity: 2},
    {name:"chair3", price: 15, quantity: 2},
    {name:"chair4", price: 15, quantity: 2},
    {name:"chair5", price: 15, quantity: 2},
    {name:"chair6", price: 15, quantity: 2},
    {name:"chair7", price: 15, quantity: 2},

  ];
  newProduct: any = {};
  payment: any = {};

  constructor(
    private httpClient: HttpClient){ }

   

  addProduct() {
    this.products.push({ ...this.newProduct });
    this.newProduct = {};
  }

  payForLastOrder(){
    let data = {
      orderId: this.lastOrderId,
      ...this.payment
    };
    this.httpClient.post<string>("http://localhost:7002", data).subscribe(value => console.log(value));
  }

  buttonHandler(){
    let data = {
      address: {
        street: "Mazowiecka 34",
        city: "Warszawa",
        zipCode: "00-000",
        country: "Polska"
      },
      customer: {
        firstName: "Jan",
        lastName: "Testowy"
      },
      products: this.products.length == 0 ? this.defaultProducts : this.products
    };
    console.log(data);
    this.httpClient.post<string>("http://localhost:7001", data).subscribe(value => this.lastOrderId = value);
  }
}
