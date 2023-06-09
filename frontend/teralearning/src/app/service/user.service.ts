import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  Url:string = "http://localhost:8080/api/user/"
  constructor(private http:HttpClient) { }

  addNewUser(data:any):Observable<any>{
    return this.http.post(this.Url + "new", data);
  }
  getAllUser(){
    return this.http.get(this.Url+"list");
  }
  deleteUser(data:any){
   return this.http.delete(this.Url+"delete/"+data.id);
  }

  updateUser(data:any){
    return this.http.put(this.Url+"update",data);
  }

  
}
