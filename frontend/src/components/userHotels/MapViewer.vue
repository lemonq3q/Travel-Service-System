<template>
  <div class="map" ref="containerRef"></div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';

const props = defineProps({
  longitude: { type: Number, required: true },
  latitude: { type: Number, required: true },
  zoom: { type: Number, default: 15 }
});

const containerRef = ref(null);

let map = null;
let marker = null;
let AMap = null;

const initMap = async () => {
  window._AMapSecurityConfig = {
    securityJsCode: 'd2cf19f99afe609da0cd05d3f2294e51'
  };

  AMap = await AMapLoader.load({
    key: '3f2c976216af0c59f0ee1e32c72aea06',
    version: '2.0',
    plugins: []
  });

  map = new AMap.Map(containerRef.value, {
    zoom: props.zoom,
    center: [props.longitude, props.latitude],
    // dragEnable: false,
    // zoomEnable: false,
    // doubleClickZoom: false
  });

  marker = new AMap.Marker({
    position: [props.longitude, props.latitude]
  });
  map.add(marker);
};

const updateCenter = () => {
  if (!map || !marker) return;
  const pos = [props.longitude, props.latitude];
  marker.setPosition(pos);
  map.setCenter(pos);
};

onMounted(() => {
  initMap();
});

watch(() => [props.longitude, props.latitude], updateCenter);

onBeforeUnmount(() => {
  if (map) {
    map.destroy();
  }
  map = null;
  marker = null;
  AMap = null;
});
</script>

<style scoped>
.map {
  width: 100%;
  height: 160px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}
</style>

