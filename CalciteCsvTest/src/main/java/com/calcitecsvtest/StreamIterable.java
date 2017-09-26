/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calcitecsvtest;

import java.util.Iterator;

/**
 *
 * @author nuwansa
 */
public class StreamIterable<T> implements Iterable<T> {

  private final Iterator<T> iterator;

  public StreamIterable(Iterator<T> iterator) {
    this.iterator = iterator;
  }

  @Override
  public Iterator<T> iterator() {
    return iterator;
  }
}