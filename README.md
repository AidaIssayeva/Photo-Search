# Photo-Search

# Important Step

Please add your Flickr API KEY to `local.properties` with the key `flickr_api_key`. The format should be like this:
```
flickr_api_key=YOUR_KEY
```
# Requirements Status 

### Functional Requirements ✅
- Provide an interface for inputting search terms ✅
- Display 25 results for the given search term, including a thumbnail of the image and the title ✅
- Selecting a thumbnail or title displays the full photo ✅
- Provide a way to return to the search results ✅
- Provide a way to search for another term ✅

### Technical Requirements ✅
- Make all calls to the Flickr REST API ✅
- Consume all API content in JSON ✅
- Use your own code to communicate with the Flickr API (Use of other third party libraries is okay) ✅

### Extra Mile (not required) 🔄
- Save prior search terms and present them as quick search options ✅
- Page results (allowing more than the initial 25 to be displayed) 🚫
- Sensible error handling and testing ✅
- iOS ONLY: Use only system frameworks for network requests and parsing (not applicable)
- Android ONLY: Ability to bookmark and view images offline 🚫