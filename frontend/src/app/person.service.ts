import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Person } from './person.model';

@Injectable({ providedIn: 'root' })
export class PersonService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/people';

  getAll(lastName?: string): Observable<Person[]> {
    let params = new HttpParams();
    if (lastName && lastName.trim()) {
      params = params.set('lastName', lastName.trim());
    }
    return this.http.get<Person[]>(this.apiUrl, { params });
  }

  create(person: Person): Observable<Person> {
    return this.http.post<Person>(this.apiUrl, person);
  }

  update(id: number, person: Person): Observable<Person> {
    return this.http.put<Person>(`${this.apiUrl}/${id}`, person);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

