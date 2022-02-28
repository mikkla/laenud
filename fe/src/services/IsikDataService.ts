import type Isik from "@/types/Isik";
import http from "../http-common";
class IsikDataService {
  getAll() {
    return http.get("/isik");
  }
  get(id: number) {
    return http.get(`/isik/${id}`);
  }
  create(data: Isik) {
    return http.post("/isik", data);
  }
  update(id: number, data: Isik) {
    return http.put(`/isik/${id}`, data);
  }
  delete(id: number) {
    return http.delete(`/isik/${id}`);
  }
}
export default new IsikDataService();

