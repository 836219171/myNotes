import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atguigu.bean.ExcelBean2;
import com.atguigu.service.SpuServiceInf;

/**
 * @author BY QinLiang Time:2018��5��11��
 */

public class ExcelUtils {
    
	/** ��ȡexcel,�ڿ���̨��ʾ */
	public static <E> List<E> readExcel(String fileName, String ClassName) {
		List<E> list = new ArrayList<E>();
		try {
			Class<?> c1 = null;

			c1 = Class.forName(ClassName);

			String filePath = fileName;
			FileInputStream is = null;
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

			is = new FileInputStream(filePath);

			// ��ù�����
			Workbook workbook = null;

			workbook = WorkbookFactory.create(is);

			// ��ù��������
			int sheetCount = workbook.getNumberOfSheets();
			// ����������
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				// �������
				int rows = sheet.getLastRowNum() + 1;
				// ����������Ȼ��һ�У��ڵõ���������
				Row tmp = sheet.getRow(0);

				// ȡ��һ������Ч�ĵ�Ԫ����
				int cols = tmp.getPhysicalNumberOfCells();
				// һ�д���һ������һ�д���һ�����������ֵ
				for (int row = 0; row < rows; row++) {
					// �ж����оʹ������ٸ�����
					Object object = null;

					// ʵ����һ������
					object = c1.newInstance();

					// fiels�����飬ѭ����ȡ����
					Field[] fields = c1.getDeclaredFields();

					Row r = sheet.getRow(row);

					for (int col = 0; col < cols; col++) {

						// fieldId Ϊbean�еĵڼ������ԣ�����col
						int fieldId = col;
						// ����ͨ����������˽������
						fields[fieldId].setAccessible(true);
						Cell cell = r.getCell(col);
						int cellType = cell.getCellType();
						// System.out.println("cellType=" + cellType+"
						// col="+col);
						String cellValue = null;
						// ����cellType������ȷ����ʲô��������������
						switch (cellType) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								// �����ڸ�ʽ��Ϊ�ַ�����
								cellValue = fmt.format(cell.getDateCellValue());
								// ���ַ�����תΪdate�Ͳ��浽������

								fields[fieldId].set(object, new java.sql.Date(
										new SimpleDateFormat("yyyy-MM-dd").parse(cellValue).getTime()));

								System.out.print("date cellValue=" + cellValue + "  ");
							} else {
								// ����
								cellValue = String.valueOf(cell.getNumericCellValue()); 
								if (null != cellValue) {
									//���͵Ĵ���
									if (IsInt(cellValue)) {
										cellValue = cellValue.substring(0, cellValue.lastIndexOf("."));
										fields[fieldId].set(object, Integer.valueOf(cellValue));
										System.out.print("int  cellValue=" + cellValue + "  ");
									} else {
										// float���ʹ���
										fields[fieldId].set(object, Double.valueOf(cellValue));
										System.out.print("double cellValue=" + cellValue + "  ");
									}
								} else {
									throw new IllegalArgumentException("�˵�Ԫ��Ϊ��");
								}
							}
							break;

						case Cell.CELL_TYPE_STRING:
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("string cellValue=" + cellValue + "  ");
							break;

						case Cell.CELL_TYPE_FORMULA:
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("cellValue cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_BLANK:
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("cellValue cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_BOOLEAN:
							cellValue = String.valueOf(cell.getBooleanCellValue());
							fields[fieldId].set(object, Boolean.valueOf(cellValue));
							System.out.print("boolean cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_ERROR:
							throw new IllegalArgumentException("����");

						default:
							break;
						}

					}
					System.out.println();
					list.add((E) object);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return list;

	}
      
	/** ����excel����Ҫ�ֶ�����ֵ */
	public static Workbook createWorkBook(List<Map<String, Object>> list, String[] keys, String columnNames[]) {
		// ����excel������
		Workbook wb = new HSSFWorkbook();
		// ������һ��sheet��ҳ����������
		Sheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
		// �ֶ������п���һ��������ʾҪΪ�ڼ����裻���ڶ���������ʾ�еĿ�ȣ�nΪ�иߵ���������
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}

		// ������һ��
		Row row = sheet.createRow((short) 0);

		// �������ֵ�Ԫ���ʽ
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// ������������
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// ������һ��������ʽ������������
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// �����ڶ���������ʽ������ֵ��
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// ���õ�һ�ֵ�Ԫ�����ʽ������������
		cs.setFont(f);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);

		// ���õڶ��ֵ�Ԫ�����ʽ������ֵ��
		cs2.setFont(f2);
		cs2.setBorderLeft(CellStyle.BORDER_THIN);
		cs2.setBorderRight(CellStyle.BORDER_THIN);
		cs2.setBorderTop(CellStyle.BORDER_THIN);
		cs2.setBorderBottom(CellStyle.BORDER_THIN);
		cs2.setAlignment(CellStyle.ALIGN_CENTER);
		// ��������
		for (int i = 0; i < columnNames.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cs);
		}
		// ����ÿ��ÿ�е�ֵ
		for (short i = 1; i < list.size(); i++) {
			// Row ��,Cell ���� , Row �� Cell ���Ǵ�0��ʼ������
			// ����һ�У���ҳsheet��
			Row row1 = sheet.createRow((short) i);
			// ��row���ϴ���һ������
			for (short j = 0; j < keys.length; j++) {
				Cell cell = row1.createCell(j);
				//Ϊÿ����Ԫ��ֵ
				cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
				cell.setCellStyle(cs2);
			}
		}
		return wb;
	} 


	
	
	/** �����ݿ⵼������ createWorkBook�Ľ�һ����װ,ʵ�ʵ���·��ΪfileName+ʱ���,
	 * listΪ�ӵ�����javaBean�ļ���*/
	public  static <E> void outExcel(String[] columnNames,List<E> list,String fileName)  {
		
		try {
			if (list != null && list.size() >0) {
			 	Class<?> c1 = list.get(0).getClass();
				Object object = c1.newInstance();
				Field[] fields = c1.getDeclaredFields();
				String[] keys = new String[fields.length];
				List<Map<String, Object>> listMap = new ArrayList<>();
				//������������,map1Ϊ��һ�Ź�����
				Map<String, Object> map1 = new HashMap<String, Object>();  
				map1.put("sheetName", "wind_data1");
				listMap.add(map1);
				 for (int i = 0; i < list.size(); i++) {
					 Map<String, Object> map = new HashMap<String, Object>(); 
					 for (int j = 0; j < fields.length; j++) {
						 fields[j].setAccessible(true);
						map.put(fields[j].toString(),contactGet(list.get(i),fields[j].getName().toString()) );
						//������Ϊmap�ļ�ֵ
						 keys[j] = fields[j].toString();
						
					}
					
					 listMap.add(map);
				}
				 
					Workbook wb = createWorkBook(listMap, keys, columnNames);
					long curTime = System.currentTimeMillis();
				    File file = new File(fileName+curTime+".xlsx");  
			        OutputStream os = new FileOutputStream(file);  
			        os.flush();  
			        wb.write(os);  
			        os.close(); 
			        System.out.println("finish");
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	   /** ����ִ�����Ե�get�������õ�����ֵ */     
	private static  Object  contactGet(Object object,String field){
		Class clazz = object.getClass();  
		Object result = null;
		 Method[] methods  =clazz.getMethods();
 		String o = field.substring(0, 1).toUpperCase()+field.substring(1);
		//
		String getMethod = "get"+o;
		System.out.println("getMethod="+getMethod);
		for (Method method : methods) {
			
			System.out.println("method="+method.getName());
			if (method.getName().equals(getMethod)) {
				try {
					//result = method.invoke(object);
					System.out.println("**********method="+method);
					result = method.invoke(object);
					System.out.println("***************"+result);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return  result;
 
		
	}
	   /** �ж��Ƿ������� */
	private static boolean IsInt(String num) {

		String preNum = num.substring(num.lastIndexOf("."));
		int isLength = preNum.length();
		if (isLength != 0) {
			if (isLength == 2 && ".0".equals(preNum)) {
				return true;
			}

		}
		return false;

	}
	

}












