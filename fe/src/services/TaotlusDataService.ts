import type Taotlus from "@/types/Taotlus";
import http from "../http-common";
class TaotlusDataService {
  getAll() {
    return http.get("/taotlus");
  }
  getForIsik(id: number) {
    return http.get(`/taotlus/${id}`);
  }
  create(id: number, data: Taotlus) {
    return http.post(`/taotlus/${id}`, data);
  }
  update(id: number, data: Taotlus) {
    return http.put(`/taotlus/${id}`, data);
  }
  delete(id: number) {
    return http.delete(`/taotlus/${id}`);
  }
}
export default new TaotlusDataService();

